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

    public Osoba() {
        this.meno = new char[MAX_VELKSOT_MENA];
        this.priezvisko = new char[MAX_VELKOST_PRIEZVISKA];
        this.id = 0;
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
    public Osoba fromByteArray(byte[] poleBytov) {
        ByteArrayInputStream in = new ByteArrayInputStream(poleBytov);
        DataInputStream dataInput = new DataInputStream(in);
        try {
            // Načítanie mena
            int dlzkaMena = dataInput.readInt();
            byte[] menoBytes = new byte[MAX_VELKSOT_MENA];
            dataInput.readFully(menoBytes);
            this.meno = Arrays.copyOf(new String(menoBytes, 0, dlzkaMena).toCharArray(), MAX_VELKSOT_MENA);

            // Načítanie priezviska
            int dlzkaPriezviska = dataInput.readInt();
            byte[] priezviskoBytes = new byte[MAX_VELKOST_PRIEZVISKA];
            dataInput.readFully(priezviskoBytes);
            this.priezvisko = Arrays.copyOf(new String(priezviskoBytes, 0, dlzkaPriezviska).toCharArray(), MAX_VELKOST_PRIEZVISKA);

            // Načítanie ID
            this.id = dataInput.readInt();
            return this;
        } catch (IOException e) {
            throw new IllegalStateException("Chyba pri deserializácii záznamu.", e);
        }
    }


    @Override
    public byte[] toByteArray() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream dataOutput = new DataOutputStream(out);
        try {
            // Zápis mena
            int dlzkaMena = (int) new String(meno).chars()
                    .filter(c -> c != '\0')
                    .count(); //TODO nespravne, opraviť!!!
            dataOutput.writeInt(dlzkaMena); // Uložiť reálnu dĺžku mena
            dataOutput.write(new String(meno).getBytes(), 0, MAX_VELKSOT_MENA); // Zapísať pevnú veľkosť

            // Zápis priezviska
            int dlzkaPriezviska = (int) new String(priezvisko).chars()
                    .filter(c -> c != '\0')
                    .count();
            dataOutput.writeInt(dlzkaPriezviska); // Uložiť reálnu dĺžku priezviska
            dataOutput.write(new String(priezvisko).getBytes(), 0, MAX_VELKOST_PRIEZVISKA); // Zapísať pevnú veľkosť

            // Zápis ID
            dataOutput.writeInt(id);
        } catch (IOException e) {
            throw new IllegalStateException("Chyba pri serializácii záznamu.", e);
        }
        return out.toByteArray();
    }

    @Override
    public int getSize() {
        return MAX_VELKSOT_MENA + MAX_VELKOST_PRIEZVISKA + Integer.BYTES; //+ (MAX_VELKOST_SERVISOV * new Servis().getSize());
    }

    @Override
    public String toString() {
        return "Osoba{" +
                "meno=" + Arrays.toString(meno) +
                ", priezvisko=" + Arrays.toString(priezvisko) +
                ", id=" + id +
                ", servisy=" + Arrays.toString(servisy) +
                '}';
    }
}
