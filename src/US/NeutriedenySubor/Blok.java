package US.NeutriedenySubor;

import rozhrania.IByteOperacie;
import rozhrania.IZaznam;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class Blok<T extends IZaznam<T>> implements IByteOperacie<T> {
    private int pocetValidnychZaznamov;
    private final IZaznam<T>[] zaznamy;
    private final int maxPocetZaznamov;
    private int dalsiVolnyIndex;
    private int predchadzajuciVolnyIndex;
    private final Class<T> typZaznamu;

    public Blok(int blokovaciFaktor, Class<T> typZaznamu) {
        this.zaznamy = (IZaznam<T>[]) new IZaznam[blokovaciFaktor];
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
            for (int i = 0; i < this.pocetValidnychZaznamov; i++) {
                byte[] zaznamBytes = new byte[velkostZaznamu];
                hlpInStream.read(zaznamBytes, 0, velkostZaznamu);
                IZaznam<T> zaznam = typZaznamu.getDeclaredConstructor().newInstance().fromByteArray(zaznamBytes);
                zaznam.fromByteArray(zaznamBytes);
                zaznamy[i] = zaznam;
            }
            return null;
        } catch (IOException | InstantiationException | NoSuchMethodException | InvocationTargetException |
                 IllegalAccessException e) {
            throw new IllegalStateException("Chyba pri konverzii z byte array.");
        }
    }

    public void vyprazdniBlok() {
        Arrays.fill(this.zaznamy, null);
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
            for (int i = 0; i < this.pocetValidnychZaznamov; i++) {
                IZaznam<T> z = zaznamy[i];
                byte[] zaznamBytes = z.toByteArray();
                hlpOutStream.write(zaznamBytes, 0, zaznamBytes.length);
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
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
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
            zaznamy[pocetValidnychZaznamov] = zaznam;
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
        for (int i = 0; i < zaznamy.length; i++) {
            if (zaznamy[i].rovnaSa(zaznam)) {
                indexZaznamu = i;
                break;
            }
        }
        if (indexZaznamu != -1) {
            if (indexZaznamu != pocetValidnychZaznamov - 1) {
                IZaznam<T> docasnyZaznam = zaznamy[indexZaznamu];
                zaznamy[indexZaznamu] = zaznamy[pocetValidnychZaznamov - 1];
                zaznamy[pocetValidnychZaznamov - 1] = docasnyZaznam;
            }
            IZaznam<T> zmazanyZaznam = zaznamy[pocetValidnychZaznamov - 1];
            zaznamy[pocetValidnychZaznamov - 1] = null;
            pocetValidnychZaznamov--;
            predchadzajuciVolnyIndex = dalsiVolnyIndex;
            dalsiVolnyIndex = pocetValidnychZaznamov;
            return zmazanyZaznam.vytvorKopiu();
        }
        return null;
    }

    public int getPocetValidnychZaznamov() {
        return pocetValidnychZaznamov;
    }

    public void vypisObsah() {
        for (IZaznam<T> z : zaznamy) {
            if (z != null) {
                System.out.println(z);
            }
        }
    }
}
