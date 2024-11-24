package triedy;

import rozhrania.IZaznam;

import java.io.*;
import java.util.Arrays;

public class Vozidlo implements IZaznam<Vozidlo> {
    private final int MAX_VELKSOT_MENA = 15;
    private final int MAX_VELKOST_PRIEZVISKA = 20;
    private final int MAX_VELKOST_ECV = 10;
    private final int MAX_VELKOST_SERVISOV = 5;

    private char[] menoZakaznika;
    private char[] priezviskoZakaznika;
    private int idZakaznika;
    private char[] ecv;
    private NavstevyServisu[] navstevyServisu;

    public Vozidlo(char[] menoZakaznika, char[] priezviskoZakaznika, int idZakaznika, char[] ecv) {
        if (menoZakaznika.length > MAX_VELKSOT_MENA) {
            throw new IllegalArgumentException("Meno môže mať maximálne " + MAX_VELKSOT_MENA + " znakov.");
        }
        this.menoZakaznika = menoZakaznika;
        if (priezviskoZakaznika.length > MAX_VELKOST_PRIEZVISKA) {
            throw new IllegalArgumentException("Priezvisko môže mať maximálne " + MAX_VELKOST_PRIEZVISKA + " znakov.");
        }
        this.priezviskoZakaznika = priezviskoZakaznika;
        this.idZakaznika = idZakaznika;
        if (ecv.length > MAX_VELKOST_ECV) {
            throw new IllegalArgumentException("ECV môže mať maximálne " + MAX_VELKOST_ECV + " znakov.");
        }
        this.ecv = ecv;
        /*if (navstevyServisu.length > MAX_VELKOST_SERVISOV) {
            throw new IllegalArgumentException("Zákazník môže mať maximálne " + MAX_VELKOST_SERVISOV + " návštev servisu.");
        }*/
        this.navstevyServisu = new NavstevyServisu[MAX_VELKOST_SERVISOV];
    }

    public Vozidlo() {
        this.menoZakaznika = new char[MAX_VELKSOT_MENA];
        this.priezviskoZakaznika = new char[MAX_VELKOST_PRIEZVISKA];
        this.idZakaznika = 0;
        this.ecv = new char[MAX_VELKOST_ECV];
        this.navstevyServisu = new NavstevyServisu[MAX_VELKOST_SERVISOV];
    }

    @Override
    public String toString() {
        return "Vozidlo{" +
                "menoZakaznika=" + Arrays.toString(menoZakaznika) +
                ", priezviskoZakaznika=" + Arrays.toString(priezviskoZakaznika) +
                ", idZakaznika=" + idZakaznika +
                ", ecv=" + Arrays.toString(ecv) +
                ", navstevyServisu=" + Arrays.toString(navstevyServisu) +
                '}';
    }

    @Override
    public boolean rovnaSa(Vozidlo objekt) {
        return this.idZakaznika == objekt.idZakaznika;
    }

    @Override
    public Vozidlo vytvorKopiu() {
        return new Vozidlo(menoZakaznika, priezviskoZakaznika, idZakaznika, ecv);
    }

    @Override
    public Vozidlo fromByteArray(byte[] poleBytov) {
        ByteArrayInputStream in = new ByteArrayInputStream(poleBytov);
        DataInputStream dataInput = new DataInputStream(in);
        try {
            byte[] menoBytes = new byte[MAX_VELKSOT_MENA];
            dataInput.readFully(menoBytes);
            this.menoZakaznika = new String(menoBytes).trim().toCharArray();

            byte[] priezviskoBytes = new byte[MAX_VELKOST_PRIEZVISKA];
            dataInput.readFully(priezviskoBytes);
            this.priezviskoZakaznika = new String(priezviskoBytes).trim().toCharArray();

            this.idZakaznika = dataInput.readInt();

            byte[] ecvBytes = new byte[MAX_VELKOST_ECV];
            dataInput.readFully(ecvBytes);
            this.ecv = new String(ecvBytes).trim().toCharArray();

            /*NavstevyServisu[] navstevyServisu = new NavstevyServisu[MAX_VELKOST_SERVISOV];
            for (int i = 0; i < MAX_VELKOST_SERVISOV; i++) {
                byte[] navstevaBytes = new byte[navstevyServisu[i].getSize()];
                dataInput.readFully(navstevaBytes);
                navstevyServisu[i] = new NavstevyServisu().fromByteArray(navstevaBytes);
            }
            this.navstevyServisu = navstevyServisu;*/
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
            dataOutput.write(String.format("%-" + MAX_VELKSOT_MENA + "s", new String(menoZakaznika)).getBytes());
            dataOutput.write(String.format("%-" + MAX_VELKOST_PRIEZVISKA + "s", new String(priezviskoZakaznika)).getBytes());
            dataOutput.writeInt(idZakaznika);
            dataOutput.write(String.format("%-" + MAX_VELKOST_ECV + "s", new String(ecv)).getBytes());
            /*for (int i = 0; i < MAX_VELKOST_SERVISOV; i++) {
                byte[] navstevaBytes = navstevyServisu[i].toByteArray();
                dataOutput.write(navstevaBytes);
            }*/
        } catch (IOException e) {
            throw new IllegalStateException("Chyba pri serializácii záznamu.", e);
        }
        return out.toByteArray();
    }

    @Override
    public int getSize() {
        return MAX_VELKSOT_MENA + MAX_VELKOST_PRIEZVISKA + Integer.BYTES + MAX_VELKOST_ECV;// + MAX_VELKOST_SERVISOV * new NavstevyServisu().getSize();
    }
}
