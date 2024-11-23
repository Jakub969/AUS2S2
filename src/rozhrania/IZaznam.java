package rozhrania;

public interface IZaznam<T> extends IByteOperacie<T> {
    boolean rovnaSa(T objekt);
    T vytvorKopiu();

    @Override
    T fromByteArray(byte[] poleBytov);

    @Override
    byte[] toByteArray();

    @Override
    int getSize();
}
