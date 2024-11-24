package triedy;

import rozhrania.IZaznam;

import java.io.*;
import java.util.Arrays;

public class Zakaznik implements IZaznam<Zakaznik> {
    private final int MAX_VELKSOT_MENA = 15;
    private final int MAX_VELKOST_PRIEZVISKA = 20;
    private final int MAX_VELKOST_ECV = 10;
    private final int MAX_VELKOST_SERVISOV = 5;

    private char[] meno;
    private char[] priezvisko;
    private int id;
    private char[] ecv;
    private NavstevyServisu[] navstevyServisu;

    public Zakaznik(char[] meno, char[] priezvisko,int id, char[] ecv, NavstevyServisu[] navstevyServisu) {
        if (meno.length > MAX_VELKSOT_MENA) {
            throw new IllegalArgumentException("Meno môže mať maximálne " + MAX_VELKSOT_MENA + " znakov.");
        }
        this.meno = meno;
        if (priezvisko.length > MAX_VELKOST_PRIEZVISKA) {
            throw new IllegalArgumentException("Priezvisko môže mať maximálne " + MAX_VELKOST_PRIEZVISKA + " znakov.");
        }
        this.priezvisko = priezvisko;
        this.id = id;
        if (ecv.length > MAX_VELKOST_ECV) {
            throw new IllegalArgumentException("ECV môže mať maximálne " + MAX_VELKOST_ECV + " znakov.");
        }
        this.ecv = ecv;
        if (navstevyServisu.length > MAX_VELKOST_SERVISOV) {
            throw new IllegalArgumentException("Zákazník môže mať maximálne " + MAX_VELKOST_SERVISOV + " návštev servisu.");
        }
        this.navstevyServisu = navstevyServisu;
    }

    public Zakaznik() {
        this.meno = new char[MAX_VELKSOT_MENA];
        this.priezvisko = new char[MAX_VELKOST_PRIEZVISKA];
        this.id = 0;
        this.ecv = new char[MAX_VELKOST_ECV];
        this.navstevyServisu = new NavstevyServisu[MAX_VELKOST_SERVISOV];
    }

    @Override
    public String toString() {
        return "Zakaznik{" +
                "meno=" + Arrays.toString(meno) +
                ", priezvisko=" + Arrays.toString(priezvisko) +
                ", id=" + id +
                ", ecv=" + Arrays.toString(ecv) +
                ", navstevyServisus=" + Arrays.toString(navstevyServisu) +
                '}';
    }

    @Override
    public boolean rovnaSa(Zakaznik objekt) {
        return this.id == objekt.id;
    }

    @Override
    public Zakaznik vytvorKopiu() {
        return new Zakaznik(meno, priezvisko, id, ecv, navstevyServisu);
    }

    @Override
    public Zakaznik fromByteArray(byte[] poleBytov) {
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

            byte[] ecvBytes = new byte[MAX_VELKOST_ECV];
            dataInput.readFully(ecvBytes);
            this.ecv = new String(ecvBytes).trim().toCharArray();

            NavstevyServisu[] navstevyServisu = new NavstevyServisu[MAX_VELKOST_SERVISOV];
            for (int i = 0; i < MAX_VELKOST_SERVISOV; i++) {
                byte[] navstevaBytes = new byte[navstevyServisu[i].getSize()];
                dataInput.readFully(navstevaBytes);
                navstevyServisu[i] = new NavstevyServisu().fromByteArray(navstevaBytes);
            }
            this.navstevyServisu = navstevyServisu;
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
            dataOutput.write(String.format("%-" + MAX_VELKSOT_MENA + "s", new String(meno)).getBytes());
            dataOutput.write(String.format("%-" + MAX_VELKOST_PRIEZVISKA + "s", new String(priezvisko)).getBytes());
            dataOutput.writeInt(id);
            dataOutput.write(String.format("%-" + MAX_VELKOST_ECV + "s", new String(ecv)).getBytes());
            for (int i = 0; i < MAX_VELKOST_SERVISOV; i++) {
                byte[] navstevaBytes = navstevyServisu[i].toByteArray();
                dataOutput.write(navstevaBytes);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Chyba pri serializácii záznamu.", e);
        }
        return out.toByteArray();
    }

    @Override
    public int getSize() {
        return MAX_VELKSOT_MENA + MAX_VELKOST_PRIEZVISKA + Integer.BYTES + MAX_VELKOST_ECV + MAX_VELKOST_SERVISOV * new NavstevyServisu().getSize();
    }
}
