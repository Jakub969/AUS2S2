public interface IZaznam<T> extends IByteOperacie {
    boolean rovnaSa(T objekt);
    T vytvorKopiu();

    @Override
    void fromByteArray(byte[] poleBytov);

    @Override
    byte[] toByteArray();

    @Override
    int getSize();
}
