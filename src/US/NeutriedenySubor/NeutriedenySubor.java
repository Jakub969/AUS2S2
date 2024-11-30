package US.NeutriedenySubor;

import rozhrania.IZaznam;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class NeutriedenySubor<T extends IZaznam<T>> {
    private int uplnePrazdnyBlok;
    private int ciastocnePrazdnyBlok;
    private Blok<T> aktualnyBlok;
    private final File subor;
    private final Class<T> typZaznamu;
    private final int velkostClustera;

    public NeutriedenySubor(String nazovSuboru, Class<T> typZaznamu, int velkostClustera) {

        this.subor = new File(nazovSuboru);
        this.uplnePrazdnyBlok = -1;
        this.ciastocnePrazdnyBlok = -1;
        this.typZaznamu = typZaznamu;
        this.aktualnyBlok = new Blok<>(velkostClustera, typZaznamu);
        this.velkostClustera = velkostClustera;
    }

    public int vlozZaznam(IZaznam<T> zaznam) {
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
            ciastocnePrazdnyBlok = adresabloku;
        }

        if (aktualnyBlok != null && aktualnyBlok.getPocetValidnychZaznamov() == aktualnyBlok.getMaxPocetZaznamov()) {
            Blok<T> novyBlok = new Blok<>(velkostClustera, typZaznamu);
            int novaAdresaBloku = najdiAdresuPrazdnehoBloku();
            aktualnyBlok.setDalsiBlok(novaAdresaBloku);
            novyBlok.setPredchadzajuciBlok(adresabloku);
            zapisBlok(aktualnyBlok, adresabloku);
            aktualnyBlok = novyBlok;
            adresabloku = novaAdresaBloku;
        }

        if (aktualnyBlok != null) {
            aktualnyBlok.vlozZaznam(zaznam);
        }

        if (aktualnyBlok != null && aktualnyBlok.getPocetValidnychZaznamov() == aktualnyBlok.getMaxPocetZaznamov()) {
            ciastocnePrazdnyBlok = -1;
        }

        if (aktualnyBlok != null) {
            zapisBlok(aktualnyBlok, adresabloku);
        }
        return adresabloku;
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
        if (najdenyBlok != null) {
            T zaznamZBloku = najdenyBlok.getZaznam(zaznam);
            najdenyBlok.vyprazdniBlok();
            return zaznamZBloku;
        }
        return null;
    }

    public T zmazZaznam(T zaznam, int blok) {
        Blok<T> najdenyBlok = citajBlok(blok);
        T zmazanyZaznam = null;
        if (najdenyBlok != null) {
            zmazanyZaznam = najdenyBlok.zmazZaznam(zaznam);
        }
        if (najdenyBlok != null) {
            ulozHlavicku(najdenyBlok, blok);
        }
        int dlzkaSuboru = (int) subor.length();
        int dlzkaBloku = aktualnyBlok.getSize();
        int poslednyBlokIndex = (dlzkaSuboru / dlzkaBloku) - 1;
        if (najdenyBlok != null && najdenyBlok.getPocetValidnychZaznamov() == 0 && blok == poslednyBlokIndex) {
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
                opravPrepojenie();

                blokIndex--;
            }

            raf.setLength((long) (blokIndex + 1) * aktualnyBlok.getSize());
        } catch (IOException e) {
            throw new IllegalStateException("Chyba pri orezávaní súboru.", e);
        }
    }

    private void opravPrepojenie() {
        Blok<T> predchadzajuciBlok = citajBlok(aktualnyBlok.getPredchadzajuciBlok());
        Blok<T> dalsiBlok = citajBlok(aktualnyBlok.getDalsiBlok());
        if (predchadzajuciBlok != null) {
            predchadzajuciBlok.setDalsiBlok(aktualnyBlok.getDalsiBlok());
            this.ciastocnePrazdnyBlok = aktualnyBlok.getPredchadzajuciBlok();
            ulozHlavicku(predchadzajuciBlok, aktualnyBlok.getPredchadzajuciBlok());
            aktualnyBlok.setPredchadzajuciBlok(-1);
        }
        if (dalsiBlok != null) {
            dalsiBlok.setPredchadzajuciBlok(aktualnyBlok.getPredchadzajuciBlok());
            ulozHlavicku(dalsiBlok, aktualnyBlok.getDalsiBlok());
            aktualnyBlok.setDalsiBlok(-1);
        }
        //TODO ako nastaviť uplnePrazdnyBlok?
    }

    public void vypisObsah() {
        ArrayList<Blok<T>> bloky = new ArrayList<>();
        try (RandomAccessFile raf = new RandomAccessFile(subor, "r")) {
            while (raf.getFilePointer() < raf.length()) {
                byte[] blokBytes = new byte[aktualnyBlok.getSize()];
                raf.read(blokBytes);
                Blok<T> blok = new Blok<>(velkostClustera, typZaznamu);
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
        if (index == -1) {
            return null;
        }
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

    public void ulozAktualnyBlok(int index) {
        if (aktualnyBlok != null) {
            ulozHlavicku(aktualnyBlok, index);
        }
    }

    private void ulozHlavicku(Blok<T> blok, int index) {
        try (RandomAccessFile raf = new RandomAccessFile(subor, "rw")) {
            raf.seek((long) index * blok.getSize());
            raf.write(blok.toByteArrayHlavicka());
        } catch (IOException e) {
            throw new IllegalStateException("Chyba pri zápise hlavičky do súboru.", e);
        }

    }
}
