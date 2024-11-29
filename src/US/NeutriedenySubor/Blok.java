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
    private int dalsiBlok;
    private int predchadzajuciBlok;
    private final Class<T> typZaznamu;

    public Blok(int velkostClustera, Class<T> typZaznamu) {
        //odpocitavam 3*Integer.BYTES, pretože mám 3 atributy typu int (poceValidnychZaznamov, dalsiVolnyIndex, predchadzajuciVolnyIndex)
        this.typZaznamu = typZaznamu;
        int velkostZaznamu = getVelkostZaznamu();
        int blokovaciFaktor = (velkostClustera - 3 * Integer.BYTES) / velkostZaznamu;
        this.zaznamy = (IZaznam<T>[]) new IZaznam[blokovaciFaktor];
        this.maxPocetZaznamov = blokovaciFaktor;
        this.dalsiBlok = -1;
        this.predchadzajuciBlok = -1;
        this.pocetValidnychZaznamov = 0;
    }

    @Override
    public T fromByteArray(byte[] poleBytov) {
        vyprazdniBlok();
        ByteArrayInputStream hlpByteArrayInputStream = new ByteArrayInputStream(poleBytov);
        DataInputStream hlpInStream = new DataInputStream(hlpByteArrayInputStream);

        try {
            this.pocetValidnychZaznamov = hlpInStream.readInt();
            this.dalsiBlok = hlpInStream.readInt();
            this.predchadzajuciBlok = hlpInStream.readInt();
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
        this.dalsiBlok = -1;
        this.predchadzajuciBlok = -1;
    }

    @Override
    public byte[] toByteArray() {
        ByteArrayOutputStream hlpByteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream hlpOutStream = new DataOutputStream(hlpByteArrayOutputStream);

        try {
            hlpOutStream.writeInt(this.pocetValidnychZaznamov);
            hlpOutStream.writeInt(this.dalsiBlok);
            hlpOutStream.writeInt(this.predchadzajuciBlok);
            for (int i = 0; i < this.pocetValidnychZaznamov; i++) {
                IZaznam<T> z = zaznamy[i];
                byte[] zaznamBytes = z.toByteArray();
                hlpOutStream.write(zaznamBytes, 0, zaznamBytes.length);
            }
            int currentSize = hlpByteArrayOutputStream.size();
            int totalSize = getSize();
            if (currentSize < totalSize) {
                byte[] emptyBytes = new byte[totalSize - currentSize];
                hlpOutStream.write(emptyBytes);
            }
            return hlpByteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Chyba pri konverzii z byte array.");
        }
    }

    public byte[] toByteArrayHlavicka() {
        ByteArrayOutputStream hlpByteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream hlpOutStream = new DataOutputStream(hlpByteArrayOutputStream);

        try {
            hlpOutStream.writeInt(this.pocetValidnychZaznamov);
            hlpOutStream.writeInt(this.dalsiBlok);
            hlpOutStream.writeInt(this.predchadzajuciBlok);
            return hlpByteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
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
            return zmazanyZaznam.vytvorKopiu();
        }
        return null;
    }

    public int getMaxPocetZaznamov() {
        return maxPocetZaznamov;
    }

    public int getPocetValidnychZaznamov() {
        return pocetValidnychZaznamov;
    }

    public void setDalsiBlok(int dalsiBlok) {
        this.dalsiBlok = dalsiBlok;
    }

    public void setPredchadzajuciBlok(int predchadzajuciBlok) {
        this.predchadzajuciBlok = predchadzajuciBlok;
    }

    public int getDalsiBlok() {
        return dalsiBlok;
    }

    public int getPredchadzajuciBlok() {
        return predchadzajuciBlok;
    }

    public void vypisObsah() {
        for (IZaznam<T> z : zaznamy) {
            if (z != null) {
                System.out.println(z);
            }
        }
    }
}
