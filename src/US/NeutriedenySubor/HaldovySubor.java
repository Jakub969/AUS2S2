package US.NeutriedenySubor;

import rozhrania.IZaznam;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class HaldovySubor<T extends IZaznam<T>> {
    private int uplnePrazdnyBlok;
    private int ciastocnePrazdnyBlok;
    private int pocetZaznamov;
    private Class<T> typZaznamu;
    private String nazovSuboru;
    private File subor;

    public HaldovySubor(String nazovSuboru, Class<T> typZaznamu, int blokovaciFaktor) {
        this.pocetZaznamov = blokovaciFaktor;
        this.typZaznamu = typZaznamu;
        this.nazovSuboru = nazovSuboru;
        this.subor = new File(nazovSuboru);
    }
    public void vlozZaznam(IZaznam<T> zaznam) {
        Blok<T> blok = citajBlok(ciastocnePrazdnyBlok);

        if (blok.getPocetValidnychZaznamov() >= pocetZaznamov) {
            ciastocnePrazdnyBlok = blok.getDalsiVolnyIndex();
            blok = citajBlok(ciastocnePrazdnyBlok);
        }
        blok.vlozZaznam(zaznam);

        if (blok.getPocetValidnychZaznamov() == pocetZaznamov) {
            uplnePrazdnyBlok = ciastocnePrazdnyBlok;
            ciastocnePrazdnyBlok = blok.getDalsiVolnyIndex();
        }
    }

    public T getZaznam(T zaznam, int blok) {
        Blok<T> najdenyBlok = citajBlok(blok);
        return najdenyBlok.getZaznam(zaznam);
    }

    public void zmazZaznam(T zaznam, int blok) {
        Blok<T> najdenyBlok = citajBlok(blok);
        najdenyBlok.zmazZaznam(zaznam);
    }

    public void vypisObsah() {
        int indexBloku = uplnePrazdnyBlok;
        while (indexBloku != -1) {
            Blok<T> blok = citajBlok(indexBloku);
            blok.vypisObsah();
            indexBloku = blok.getDalsiVolnyIndex();
        }
    }

    private Blok<T> citajBlok(int index) {
        Blok<T> blok = new Blok<>(pocetZaznamov);
        try (RandomAccessFile raf = new RandomAccessFile(subor, "rw")) {
            raf.seek((long) index * blok.getSize());
            byte[] blokBytes = new byte[blok.getSize()];
            raf.read(blokBytes);
            blok.fromByteArray(blokBytes);
        } catch (IOException e) {
            throw new IllegalStateException("Chyba pri čítaní bloku zo súboru.", e);
        }
        return blok;
    }

    private void zapisBlok(Blok<T> blok, int index) {
        try (RandomAccessFile raf = new RandomAccessFile(subor, "rw")) {
            raf.seek((long) index * blok.getSize());
            raf.write(blok.toByteArray());
        } catch (IOException e) {
            throw new IllegalStateException("Chyba pri zápise bloku do súboru.", e);
        }
    }
}
