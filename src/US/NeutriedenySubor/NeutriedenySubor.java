package US.NeutriedenySubor;

import rozhrania.IZaznam;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashSet;
import java.util.Set;

public class NeutriedenySubor<T extends IZaznam<T>> {
    private int uplnePrazdnyBlok;
    private int ciastocnePrazdnyBlok;
    private final int pocetZaznamov;
    private Blok<T> aktualnyBlok;
    private final File subor;

    public NeutriedenySubor(String nazovSuboru, Class<T> typZaznamu, int blokovaciFaktor) {
        this.pocetZaznamov = blokovaciFaktor;
        this.subor = new File(nazovSuboru);
        this.uplnePrazdnyBlok = -1;
        this.ciastocnePrazdnyBlok = -1;
        this.aktualnyBlok = new Blok<>(pocetZaznamov, typZaznamu);
    }

    public void vlozZaznam(IZaznam<T> zaznam) {
        int adresabloku;

        if (uplnePrazdnyBlok != -1) {
            adresabloku = uplnePrazdnyBlok;
            aktualnyBlok = citajBlok(adresabloku);
            uplnePrazdnyBlok = -1;
        } else if (ciastocnePrazdnyBlok != -1) {
            adresabloku = ciastocnePrazdnyBlok;
            aktualnyBlok = citajBlok(adresabloku);
        } else {
            adresabloku = najdiAdresuPrazdnehoBloku();
            aktualnyBlok.vyprazdniBlok();
        }

        aktualnyBlok.vlozZaznam(zaznam);

        if (aktualnyBlok.getPocetValidnychZaznamov() == pocetZaznamov) {
            ciastocnePrazdnyBlok = -1;
        } else {
            ciastocnePrazdnyBlok = adresabloku;
        }

        zapisBlok(aktualnyBlok, adresabloku);
    }

    private int najdiAdresuPrazdnehoBloku() {
        if (uplnePrazdnyBlok != -1) {
            return uplnePrazdnyBlok;
        }
        if (ciastocnePrazdnyBlok != -1) {
            return ciastocnePrazdnyBlok;
        }
        int dlzkaSuboru = (int) subor.length();
        int dlzkaBloku = aktualnyBlok.getSize();
        return dlzkaSuboru / dlzkaBloku;
    }

    public T getZaznam(T zaznam, int blok) {
        Blok<T> najdenyBlok = citajBlok(blok);
        return najdenyBlok.getZaznam(zaznam);
    }

    public T zmazZaznam(T zaznam, int blok) {
        Blok<T> najdenyBlok = citajBlok(blok);
        T zmazanyZaznam = najdenyBlok.zmazZaznam(zaznam);
        zapisBlok(najdenyBlok, blok);
        if (najdenyBlok.getPocetValidnychZaznamov() == 0) {
            orezSuborSPrazdnymBlokomNaKonci(blok);
        }
        return zmazanyZaznam;
    }

    private void orezSuborSPrazdnymBlokomNaKonci(int blokIndex) {
        int poslednyBlokIndex = (int) (subor.length() / aktualnyBlok.getSize()) - 1;

        if (blokIndex == poslednyBlokIndex) {
            try (RandomAccessFile raf = new RandomAccessFile(subor, "rw")) {
                raf.setLength((long) blokIndex * aktualnyBlok.getSize());
            } catch (IOException e) {
                throw new IllegalStateException("Chyba pri orezávaní súboru.", e);
            }
        }
    }

    public void vypisObsah() {
        int indexBloku = 0;
        Set<Integer> visitedBlocks = new HashSet<>(); // Track visited blocks to detect cycles

        while (indexBloku != -1) {
            if (visitedBlocks.contains(indexBloku)) {
                System.out.println("Infinite loop detected at block index: " + indexBloku);
                break;
            }
            visitedBlocks.add(indexBloku);

            Blok<T> blok = citajBlok(indexBloku);
            blok.vypisObsah();
            indexBloku = blok.getDalsiVolnyIndex();

            System.out.println("Moving to next block index: " + indexBloku); // Debug log
        }
    }

    public void vycistiUdaje() {
        aktualnyBlok = null;
        uplnePrazdnyBlok = -1;
        ciastocnePrazdnyBlok = -1;
    }

    private Blok<T> citajBlok(int index) {
        try (RandomAccessFile raf = new RandomAccessFile(subor, "rw")) {
            raf.seek((long) index * aktualnyBlok.getSize());
            byte[] blokBytes = new byte[aktualnyBlok.getSize()];
            raf.read(blokBytes);
            aktualnyBlok.fromByteArray(blokBytes);
        } catch (IOException e) {
            throw new IllegalStateException("Chyba pri čítaní bloku zo súboru.", e);
        }
        return aktualnyBlok;
    }

    private void zapisBlok(Blok<T> blok, int index) {
        try (RandomAccessFile raf = new RandomAccessFile(subor, "rw")) {
            raf.seek((long) index * blok.getSize());
            raf.write(blok.toByteArray());
        } catch (IOException e) {
            throw new IllegalStateException("Chyba pri zápise bloku do súboru.", e);
        }
    }

    public void ulozAktualnyBlok() {
    if (aktualnyBlok != null) {
        int indexBloku = najdiAdresuPrazdnehoBloku();
        zapisBlok(aktualnyBlok, indexBloku);
    }
}
}
