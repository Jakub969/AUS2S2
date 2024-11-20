package triedy;

import rozhrania.IZaznam;

import java.io.*;

public class Osoba implements IZaznam<Osoba> {
    private final int MAX_VELKSOT_MENA = 15;
    private final int MAX_VELKOST_PRIEZVISKA = 20;
    private final int MAX_VELKOST_SERVISOV = 5;

    private String meno;
    private String priezvisko;
    private int id;
    private Servis[] servisy;

    public Osoba(String meno, String priezvisko, int id) {
        if (meno.length() > MAX_VELKSOT_MENA) {
            throw new IllegalArgumentException("Meno môže mať maximálne " + MAX_VELKSOT_MENA + " znakov.");
        }
        this.meno = meno;
        if (priezvisko.length() > MAX_VELKOST_PRIEZVISKA) {
            throw new IllegalArgumentException("Priezvisko môže mať maximálne " + MAX_VELKOST_PRIEZVISKA + " znakov.");
        }
        this.priezvisko = priezvisko;
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
            byte[] menoBytes = new byte[15];
            dataInput.readFully(menoBytes);
            this.meno = new String(menoBytes).trim();

            byte[] priezviskoBytes = new byte[20];
            dataInput.readFully(priezviskoBytes);
            this.priezvisko = new String(priezviskoBytes).trim();

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
            dataOutput.write(String.format("%-15s", meno).getBytes());
            dataOutput.write(String.format("%-20s", priezvisko).getBytes());
            dataOutput.writeInt(id);
        } catch (IOException e) {
            throw new IllegalStateException("Chyba pri serializácii záznamu.", e);
        }
        return out.toByteArray();
    }

    @Override
    public int getSize() {
        int velkostServisu = servisy[0].getSize();
        return 15 + 20 + Integer.BYTES + (velkostServisu * MAX_VELKOST_SERVISOV);
    }
}
