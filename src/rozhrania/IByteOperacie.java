package rozhrania;

public interface IByteOperacie<T> {
    T fromByteArray(byte[] poleBytov);
    byte[] toByteArray();
    int getSize();
}
