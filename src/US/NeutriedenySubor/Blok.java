package US.NeutriedenySubor;

import rozhrania.IByteOperacie;
import rozhrania.IZaznam;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class Blok<T extends IZaznam<T>> implements IByteOperacie {
    private int pocetValidnychZaznamov;
    private ArrayList<IZaznam<T>> zaznamy;
    private final int maxPocetZaznamov;
    private int dalsiVolnyIndex;
    private int predchadzajuciVolnyIndex;
    private final Class<T> typZaznamu;

    public Blok(int blokovaciFaktor, Class<T> typZaznamu) {
        this.zaznamy = new ArrayList<>(blokovaciFaktor);
        this.typZaznamu = typZaznamu;
        this.maxPocetZaznamov = blokovaciFaktor;
        this.dalsiVolnyIndex = -1;
        this.predchadzajuciVolnyIndex = -1;
        this.pocetValidnychZaznamov = 0;
    }

    @Override
    public T fromByteArray(byte[] poleBytov) {
        vyprazdniBlok();
        ByteArrayInputStream hlpByteArrayInputStream = new ByteArrayInputStream(poleBytov);
        DataInputStream hlpInStream = new DataInputStream(hlpByteArrayInputStream);

        try {
            this.pocetValidnychZaznamov = hlpInStream.readInt();
            this.dalsiVolnyIndex = hlpInStream.readInt();
            this.predchadzajuciVolnyIndex = hlpInStream.readInt();
            int velkostZaznamu = getVelkostZaznamu();
            int offset = Integer.BYTES + Integer.BYTES + Integer.BYTES;
            for (int i = 0; i < this.pocetValidnychZaznamov; i++) {
                byte[] zaznamBytes = new byte[velkostZaznamu];
                hlpInStream.read(zaznamBytes, 0, velkostZaznamu);
                IZaznam<T> zaznam = typZaznamu.getDeclaredConstructor().newInstance().fromByteArray(zaznamBytes);
                zaznam.fromByteArray(zaznamBytes);
                zaznamy.add(zaznam);
                offset += velkostZaznamu;
            }
            return null;
        } catch (IOException | InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new IllegalStateException("Chyba pri konverzii z byte array.");
        }
    }

    private void vyprazdniBlok() {
        this.zaznamy.clear();
        this.pocetValidnychZaznamov = 0;
        this.dalsiVolnyIndex = -1;
        this.predchadzajuciVolnyIndex = -1;
    }

    @Override
    public byte[] toByteArray() {
        ByteArrayOutputStream hlpByteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream hlpOutStream = new DataOutputStream(hlpByteArrayOutputStream);

        try {
            hlpOutStream.writeInt(this.pocetValidnychZaznamov);
            hlpOutStream.writeInt(this.dalsiVolnyIndex);
            hlpOutStream.writeInt(this.predchadzajuciVolnyIndex);
            int velkostZaznamu = getVelkostZaznamu();
            int offset = Integer.BYTES + Integer.BYTES + Integer.BYTES;
            for (int i = 0; i < this.pocetValidnychZaznamov; i++) {
                IZaznam<T> z = zaznamy.get(i);
                byte[] zaznamBytes = z.toByteArray();
                hlpOutStream.write(zaznamBytes, 0, zaznamBytes.length);
                offset += velkostZaznamu;
            }
            return hlpByteArrayOutputStream.toByteArray();


        } catch (IOException e) {
            throw new IllegalStateException("Chyba pri konverzii z byte array.");
        }
    }

    @Override
    public int getSize() {
        int velkostZaznamu = getVelkostZaznamu();
        return Integer.BYTES + Integer.BYTES + Integer.BYTES + (velkostZaznamu * maxPocetZaznamov);
    }

    private int getVelkostZaznamu() {
    try {
        T instance = typZaznamu.getDeclaredConstructor().newInstance();
        return instance.getSize();
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
        throw new IllegalStateException("Chyba pri získavaní veľkosti záznamu.", e);
    }
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
        if (pocetValidnychZaznamov < maxPocetZaznamov) {
            zaznamy.add(zaznam);
            pocetValidnychZaznamov++;
            if (pocetValidnychZaznamov == maxPocetZaznamov) {
                dalsiVolnyIndex = -1;
            } else {
                dalsiVolnyIndex = pocetValidnychZaznamov;
            }
        } else {
            throw new IllegalStateException("US.NeutriedenySubor.Blok je plný.");
        }
    }

    public T zmazZaznam(T zaznam) {
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
            return zmazanyZaznam.vytvorKopiu();
        }
        return null;
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
            System.out.println(z.toString());
        }
    }
}
