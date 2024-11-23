package triedy;

import rozhrania.IZaznam;

import java.io.*;
import java.util.Arrays;

public class Osoba implements IZaznam<Osoba> {
    private final int MAX_VELKSOT_MENA = 15;
    private final int MAX_VELKOST_PRIEZVISKA = 20;
    private final int MAX_VELKOST_SERVISOV = 5;

    private char[] meno;
    private char[] priezvisko;
    private int id;
    private Servis[] servisy;

    public Osoba(char[] meno, char[] priezvisko, int id) {
        if (meno.length > MAX_VELKSOT_MENA) {
            throw new IllegalArgumentException("Meno môže mať maximálne " + MAX_VELKSOT_MENA + " znakov.");
        }
        this.meno = Arrays.copyOf(meno, MAX_VELKSOT_MENA);
        if (priezvisko.length > MAX_VELKOST_PRIEZVISKA) {
            throw new IllegalArgumentException("Priezvisko môže mať maximálne " + MAX_VELKOST_PRIEZVISKA + " znakov.");
        }
        this.priezvisko = Arrays.copyOf(priezvisko, MAX_VELKOST_PRIEZVISKA);
        this.id = id;
        this.servisy = new Servis[MAX_VELKOST_SERVISOV];
    }

    @Override
    public boolean rovnaSa(Osoba objekt) {
        return this.id == objekt.id;
    }

    @Override
    public Osoba vytvorKopiu() {
        return new Osoba(meno, priezvisko, id);
    }

    @Override
    public void fromByteArray(byte[] poleBytov) {
        ByteArrayInputStream in = new ByteArrayInputStream(poleBytov);
        DataInputStream dataInput = new DataInputStream(in);
        try {
            byte[] menoBytes = new byte[MAX_VELKSOT_MENA];
            dataInput.readFully(menoBytes);
            this.meno = new String(menoBytes).trim().toCharArray();

            byte[] priezviskoBytes = new byte[MAX_VELKOST_PRIEZVISKA];
            dataInput.readFully(priezviskoBytes);
            this.priezvisko = new String(priezviskoBytes).trim().toCharArray();

            this.id = dataInput.readInt();
        } catch (IOException e) {
            throw new IllegalStateException("Chyba pri deserializácii záznamu.", e);
        }
    }

    @Override
    public byte[] toByteArray() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream dataOutput = new DataOutputStream(out);
        try {
            dataOutput.write(String.format("%-" + MAX_VELKSOT_MENA + "s", new String(meno)).getBytes());
            dataOutput.write(String.format("%-" + MAX_VELKOST_PRIEZVISKA + "s", new String(priezvisko)).getBytes());
            dataOutput.writeInt(id);
        } catch (IOException e) {
            throw new IllegalStateException("Chyba pri serializácii záznamu.", e);
        }
        return out.toByteArray();
    }

    @Override
    public int getSize() {
        int velkostServisu = servisy[0].getSize();
        return MAX_VELKSOT_MENA + MAX_VELKOST_PRIEZVISKA + Integer.BYTES + (velkostServisu * MAX_VELKOST_SERVISOV);
    }
}
