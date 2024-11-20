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

    public HaldovySubor(String nazovSuboru, Class<T> typZaznamu, int blokovaciFaktor) {
        this.pocetZaznamov = blokovaciFaktor;
        this.typZaznamu = typZaznamu;
        this.nazovSuboru = nazovSuboru;
    }
    public void vlozZaznam(IZaznam<T> zaznam) {
        Blok<T> blok = getBlok(ciastocnePrazdnyBlok);

        if (blok.getPocetValidnychZaznamov() >= pocetZaznamov) {
            ciastocnePrazdnyBlok = blok.getDalsiVolnyIndex();
            blok = getBlok(ciastocnePrazdnyBlok);
        }
        blok.vlozZaznam(zaznam);

        if (blok.getPocetValidnychZaznamov() == pocetZaznamov) {
            uplnePrazdnyBlok = ciastocnePrazdnyBlok;
            ciastocnePrazdnyBlok = blok.getDalsiVolnyIndex();
        }
    }

    private Blok<T> getBlok(int indexBloku) {
        File file = new File(nazovSuboru);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new IllegalStateException("Chyba pri vytváraní súboru.", e);
            }
        }

        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            long offset = indexBloku * (long) new Blok<>(pocetZaznamov).getSize();
            byte[] blokBytes = new byte[new Blok<>(pocetZaznamov).getSize()];
            raf.seek(offset);
            raf.readFully(blokBytes);
            Blok<T> blok = new Blok<>(pocetZaznamov);
            blok.fromByteArray(blokBytes);
            return blok;
        } catch (IOException e) {
            throw new IllegalStateException("Chyba pri načítaní bloku.", e);
        }
}

    public T getZaznam(T zaznam, int blok) {
        Blok<T> najdenyBlok = getBlok(blok);
        return najdenyBlok.getZaznam(zaznam);
    }

    public void zmazZaznam(T zaznam, int blok) {
        Blok<T> najdenyBlok = getBlok(blok);
        najdenyBlok.zmazZaznam(zaznam);
    }

    public void vypisObsah() {
        int indexBloku = uplnePrazdnyBlok;
        while (indexBloku != -1) {
            Blok<T> blok = getBlok(indexBloku);
            blok.vypisObsah();
            indexBloku = blok.getDalsiVolnyIndex();
        }
    }
}
