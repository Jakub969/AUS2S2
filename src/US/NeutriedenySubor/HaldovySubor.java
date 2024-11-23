package US.NeutriedenySubor;

import rozhrania.IZaznam;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashSet;
import java.util.Set;

public class HaldovySubor<T extends IZaznam<T>> {
    private int uplnePrazdnyBlok;
    private int ciastocnePrazdnyBlok;
    private int pocetZaznamov;
    private Blok<T> aktualnyBlok;
    private File subor;

    public HaldovySubor(String nazovSuboru, Class<T> typZaznamu, int blokovaciFaktor) {
        this.pocetZaznamov = blokovaciFaktor;
        this.subor = new File(nazovSuboru);
        this.uplnePrazdnyBlok = -1;
        this.ciastocnePrazdnyBlok = -1;
        this.aktualnyBlok = new Blok<>(pocetZaznamov, typZaznamu);
    }
    public void vlozZaznam(IZaznam<T> zaznam) {
        int adresabloku = najdiAdresuPrazdnehoBloku();

        Blok<T> blok = citajBlok(adresabloku);

        if (blok.getPocetValidnychZaznamov() >= pocetZaznamov) {
            ciastocnePrazdnyBlok = blok.getDalsiVolnyIndex();
            blok = citajBlok(adresabloku);
        }
        blok.vlozZaznam(zaznam);

        if (blok.getPocetValidnychZaznamov() == pocetZaznamov) {
            uplnePrazdnyBlok = ciastocnePrazdnyBlok;
            ciastocnePrazdnyBlok = blok.getDalsiVolnyIndex();
        }
        zapisBlok(blok, adresabloku);
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

    public void zmazZaznam(T zaznam, int blok) {
        Blok<T> najdenyBlok = citajBlok(blok);
        najdenyBlok.zmazZaznam(zaznam);
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


    private Blok<T> citajBlok(int index) {
        Blok<T> blok = aktualnyBlok;
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
