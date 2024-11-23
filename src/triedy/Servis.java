package triedy;

import rozhrania.IZaznam;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Arrays;
import java.util.Date;

public class Servis implements IZaznam<Servis> {
    private final int MAX_VELKOST_POPISU = 20;

    private Date datum;
    private Double cena;
    private char[] popis;
    private int id;

    public Servis(Date datum, Double cena, char[] popis,int id) {
        this.datum = datum;
        this.cena = cena;
        if (popis.length > MAX_VELKOST_POPISU) {
            throw new IllegalArgumentException("Popis môže mať maximálne " + MAX_VELKOST_POPISU + " znakov.");
        }
        this.popis = Arrays.copyOf(popis, MAX_VELKOST_POPISU);
        this.id = id;
    }

    public Servis() {
        this.datum = new Date();
        this.cena = 0.0;
        this.popis = new char[MAX_VELKOST_POPISU];
        this.id = 0;
    }

    @Override
    public boolean rovnaSa(Servis objekt) {
        return this.id == objekt.id;
    }

    @Override
    public Servis vytvorKopiu() {
        return new Servis(datum, cena, popis, id);
    }

    @Override
    public Servis fromByteArray(byte[] poleBytov) {
        ByteArrayInputStream in = new ByteArrayInputStream(poleBytov);
        DataInputStream dataInput = new DataInputStream(in);
        try {
            this.datum = new Date(dataInput.readLong());
            this.cena = dataInput.readDouble();
            byte[] popisBytes = new byte[MAX_VELKOST_POPISU];
            dataInput.readFully(popisBytes);
            this.popis = new String(popisBytes).trim().toCharArray();
            this.id = dataInput.readInt();
            return this;
        } catch (Exception e) {
            throw new IllegalStateException("Chyba pri deserializácii záznamu.", e);
        }
    }

    @Override
    public byte[] toByteArray() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream dataOutput = new DataOutputStream(out);
        try {
            dataOutput.writeLong(datum.getTime());
            dataOutput.writeDouble(cena);
            byte[] popisBytes = new byte[MAX_VELKOST_POPISU];
            for (int i = 0; i < popis.length; i++) {
                popisBytes[i] = (byte) popis[i];
            }
            dataOutput.write(popisBytes);
            dataOutput.writeInt(id);
        } catch (Exception e) {
            throw new IllegalStateException("Chyba pri serializácii záznamu.", e);
        }
        return out.toByteArray();
    }

    @Override
    public int getSize() {
        return Long.BYTES + Double.BYTES + MAX_VELKOST_POPISU + Integer.BYTES;
    }

    @Override
    public String toString() {
        return "Servis{" +
                "datum=" + datum +
                ", cena=" + cena +
                ", popis=" + Arrays.toString(popis) +
                ", id=" + id +
                '}';
    }
}
