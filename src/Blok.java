public class Blok<T extends IZaznam<T>> implements IByteOperacie {
    private int pocetValidnychZaznamov;
    private IZaznam<T>[] zaznamy;
    private int dalsiVolnyIndex;

    public Blok(int pocetValidnychZaznamov, IZaznam<T>[] zaznamy, int dalsiVolnyIndex) {
        this.pocetValidnychZaznamov = pocetValidnychZaznamov;
        this.zaznamy = zaznamy;
        this.dalsiVolnyIndex = dalsiVolnyIndex;
    }

    @Override
    public void fromByteArray(byte[] poleBytov) {

    }

    @Override
    public byte[] toByteArray() {
        return new byte[0];
    }

    @Override
    public int getSize() {
        return 0;
    }

    public T getZaznam(T zaznam) {
        for (IZaznam<T> z : zaznamy) {
            if (z.rovnaSa(zaznam)) {
                return z.vytvorKopiu();
            }
        }
        return null;
    }

    public int predchadzajuciBlok() {
        return 0;
    }
}
