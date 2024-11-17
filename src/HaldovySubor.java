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
    try (RandomAccessFile file = new RandomAccessFile(nazovSuboru, "r")) {
        long offset = indexBloku * (long) new Blok<>(pocetZaznamov).getSize();
        byte[] blokBytes = new byte[new Blok<>(pocetZaznamov).getSize()];
        file.seek(offset);
        file.readFully(blokBytes);
        Blok<T> blok = new Blok<>(pocetZaznamov);
        blok.fromByteArray(blokBytes);
        return blok;
    } catch (IOException e) {
        throw new IllegalStateException("Chyba pri načítaní bloku.", e);
    }
}

    public T getZaznam(T zaznam, int blok) {
        return null;
    }

    public void zmazZaznam(T zaznam, int blok) {

    }

    public void vypisObsah() {

    }
}
