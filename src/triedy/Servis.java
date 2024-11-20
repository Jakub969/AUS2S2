package triedy;

import rozhrania.IZaznam;

import java.util.Date;

public class Servis implements IZaznam<Servis> {
    private final int MAX_VELKOST_POPISU = 20;

    private Date datum;
    private Double cena;
    private String popis;

    public Servis(Date datum, Double cena, String popis) {
        this.datum = datum;
        this.cena = cena;
        if (popis.length() > MAX_VELKOST_POPISU) {
            throw new IllegalArgumentException("Popis môže mať maximálne " + MAX_VELKOST_POPISU + " znakov.");
        }
        this.popis = popis;
    }

    @Override
    public boolean rovnaSa(Servis objekt) {
        return false;
    }

    @Override
    public Servis vytvorKopiu() {
        return null;
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
}
