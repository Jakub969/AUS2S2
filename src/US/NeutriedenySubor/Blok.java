package US.NeutriedenySubor;

import rozhrania.IByteOperacie;
import rozhrania.IZaznam;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
public class Blok<T extends IZaznam<T>> implements IByteOperacie {
    private int pocetValidnychZaznamov;
    private ArrayList<IZaznam<T>> zaznamy;
    private int pocetZaznamov;
    private int dalsiVolnyIndex;
    private int predchadzajuciVolnyIndex;

    public Blok(int blokovaciFaktor) {
        this.zaznamy = new ArrayList<>(blokovaciFaktor);
        this.pocetZaznamov = blokovaciFaktor;
        this.dalsiVolnyIndex = -1;
        this.predchadzajuciVolnyIndex = -1;
        this.pocetValidnychZaznamov = 0;
    }

    @Override
    public void fromByteArray(byte[] poleBytov) {
        ByteArrayInputStream hlpByteArrayInputStream = new ByteArrayInputStream(poleBytov);
        DataInputStream hlpInStream = new DataInputStream(hlpByteArrayInputStream);

        try {
            this.pocetValidnychZaznamov = hlpInStream.readInt();
            this.dalsiVolnyIndex = hlpInStream.readInt();
            this.predchadzajuciVolnyIndex = hlpInStream.readInt();
            int velkostZaznamu = 0;
            if (!zaznamy.isEmpty()) {
                velkostZaznamu = zaznamy.getFirst().getSize();
            }
            int offset = Integer.BYTES + Integer.BYTES + Integer.BYTES;
            for (int i = 0; i < pocetZaznamov; i++) {
                byte[] zaznamBytes = new byte[velkostZaznamu];
                hlpInStream.read(zaznamBytes, 0, velkostZaznamu);
                IZaznam<T> zaznam = zaznamy.get(i);
                zaznam.fromByteArray(zaznamBytes);
                offset += velkostZaznamu;
            }

        } catch (IOException e) {
            throw new IllegalStateException("Chyba pri konverzii z byte array.");
        }
    }

    @Override
    public byte[] toByteArray() {
        ByteArrayOutputStream hlpByteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream hlpOutStream = new DataOutputStream(hlpByteArrayOutputStream);

        try {
            hlpOutStream.writeInt(this.pocetValidnychZaznamov);
            hlpOutStream.writeInt(this.dalsiVolnyIndex);
            hlpOutStream.writeInt(this.predchadzajuciVolnyIndex);
            int velkostZaznamu = zaznamy.getFirst().getSize();
            int offset = Integer.BYTES + Integer.BYTES + Integer.BYTES;
            for (IZaznam<T> z : zaznamy) {
                byte[] zaznamBytes = z.toByteArray();
                hlpOutStream.write(zaznamBytes, 0, zaznamBytes.length);
                offset += velkostZaznamu;
            }
            return hlpByteArrayOutputStream.toByteArray();


        } catch (IOException e) {
            throw new IllegalStateException("Chyba pri konverzii do byte array.");
        }
    }

    @Override
    public int getSize() {
        int velkostZaznamu = 0;
        if (!zaznamy.isEmpty()) {
            velkostZaznamu = zaznamy.getFirst().getSize();
        }
        return Integer.BYTES + Integer.BYTES + Integer.BYTES + (velkostZaznamu * pocetZaznamov);
    }

    public T getZaznam(T zaznam) {
        for (IZaznam<T> z : zaznamy) {
            if (z.rovnaSa(zaznam)) {
                return z.vytvorKopiu();
            }
        }
        return null;
    }

    public void vlozZaznam(IZaznam<T> zaznam) {
    if (pocetValidnychZaznamov < pocetZaznamov) {
        zaznamy.add(zaznam);
        pocetValidnychZaznamov++;
    } else {
        throw new IllegalStateException("US.NeutriedenySubor.Blok je plný.");
    }
    }

    public void zmazZaznam(T zaznam) {
        int indexZaznamu = -1;
        for (int i = 0; i < zaznamy.size(); i++) {
            if (zaznamy.get(i).rovnaSa(zaznam)) {
                indexZaznamu = i;
                break;
            }
        }
        if (indexZaznamu != -1) {
            if (indexZaznamu != pocetValidnychZaznamov - 1) {
                IZaznam<T> docasnyZaznam = zaznamy.get(indexZaznamu);
                zaznamy.set(indexZaznamu, zaznamy.get(pocetValidnychZaznamov - 1));
                zaznamy.set(pocetValidnychZaznamov - 1, docasnyZaznam);
            }
            IZaznam<T> zmazanyZaznam = zaznamy.remove(pocetValidnychZaznamov - 1);
            pocetValidnychZaznamov--;
        }
    }

    public int getDalsiVolnyIndex() {
        return dalsiVolnyIndex;
    }

    public void setDalsiVolnyIndex(int dalsiVolnyIndex) {
        this.dalsiVolnyIndex = dalsiVolnyIndex;
    }

    public int getPredchadzajuciVolnyIndex() {
        return predchadzajuciVolnyIndex;
    }

    public void setPredchadzajuciVolnyIndex(int predchadzajuciVolnyIndex) {
        this.predchadzajuciVolnyIndex = predchadzajuciVolnyIndex;
    }

    public int getPocetValidnychZaznamov() {
        return pocetValidnychZaznamov;
    }

    public void setPocetValidnychZaznamov(int pocetValidnychZaznamov) {
        this.pocetValidnychZaznamov = pocetValidnychZaznamov;
    }

    public void vypisObsah() {
        for (IZaznam<T> z : zaznamy) {
            System.out.println(z);
        }
    }
}
