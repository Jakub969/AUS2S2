package US.NeutriedenySubor;

import rozhrania.IZaznam;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class NeutriedenySubor<T extends IZaznam<T>> {
    private int uplnePrazdnyBlok;
    private int ciastocnePrazdnyBlok;
    private final int pocetZaznamov;
    private Blok<T> aktualnyBlok;
    private final File subor;
    private final Class<T> typZaznamu;

    public NeutriedenySubor(String nazovSuboru, Class<T> typZaznamu, int blokovaciFaktor) {
        this.pocetZaznamov = blokovaciFaktor;
        this.subor = new File(nazovSuboru);
        this.uplnePrazdnyBlok = -1;
        this.ciastocnePrazdnyBlok = -1;
        this.typZaznamu = typZaznamu;
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
        }

        if (aktualnyBlok.getPocetValidnychZaznamov() == pocetZaznamov) {
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
        int poslednyBlokIndex = (int) subor.length() / aktualnyBlok.getSize();
        if (najdenyBlok.getPocetValidnychZaznamov() == 0 && blok == poslednyBlokIndex) {
            orezSuborSPrazdnymBlokomNaKonci(blok);
        }
        return zmazanyZaznam;
    }

    private void orezSuborSPrazdnymBlokomNaKonci(int blokIndex) {
        try (RandomAccessFile raf = new RandomAccessFile(subor, "rw")) {

            while (blokIndex >= 0) {
                raf.seek((long) blokIndex * aktualnyBlok.getSize());
                byte[] blokBytes = new byte[aktualnyBlok.getSize()];
                raf.read(blokBytes);
                aktualnyBlok.fromByteArray(blokBytes);

                if (aktualnyBlok.getPocetValidnychZaznamov() > 0) {
                    break;
                }

                blokIndex--;
            }

            raf.setLength((long) (blokIndex + 1) * aktualnyBlok.getSize());
        } catch (IOException e) {
            throw new IllegalStateException("Chyba pri orezávaní súboru.", e);
        }
    }

    public void vypisObsah() {
        ArrayList<Blok<T>> bloky = new ArrayList<>();
        try (RandomAccessFile raf = new RandomAccessFile(subor, "r")) {
            while (raf.getFilePointer() < raf.length()) {
                byte[] blokBytes = new byte[aktualnyBlok.getSize()];
                raf.read(blokBytes);
                Blok<T> blok = new Blok<>(pocetZaznamov, typZaznamu);
                blok.fromByteArray(blokBytes);
                bloky.add(blok);
            }
            for (int i = 0; i < bloky.size(); i++) {
                System.out.println("Blok " + i + ":");
                bloky.get(i).vypisObsah();
            }
        } catch (IOException e) {
            throw new IllegalStateException("Chyba pri čítaní blokov zo súboru.", e);
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
