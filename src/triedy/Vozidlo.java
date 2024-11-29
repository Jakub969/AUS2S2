package triedy;

import rozhrania.IZaznam;

import java.io.*;
import java.util.Arrays;

public class Vozidlo implements IZaznam<Vozidlo> {
    private final int MAX_VELKOST_MENA = 15;
    private final int MAX_VELKOST_PRIEZVISKA = 20;
    private final int MAX_VELKOST_ECV = 10;
    private final int MAX_VELKOST_SERVISOV = 5;

    private char[] menoZakaznika;
    private final int skutocnaDlzkaMena;
    private char[] priezviskoZakaznika;
    private final int skutocnaDlzkaPriezviska;
    private int idZakaznika;
    private char[] ecv;
    private final int skutocnaDlzkaEcv;
    private NavstevyServisu[] navstevyServisu;

    public Vozidlo(char[] menoZakaznika, char[] priezviskoZakaznika, int idZakaznika, char[] ecv) {
        if (menoZakaznika.length > MAX_VELKOST_MENA) {
            throw new IllegalArgumentException("Meno môže mať maximálne " + MAX_VELKOST_MENA + " znakov.");
        }
        this.menoZakaznika = new char[MAX_VELKOST_MENA];
        System.arraycopy(menoZakaznika, 0, this.menoZakaznika, 0, menoZakaznika.length);
        this.skutocnaDlzkaMena = menoZakaznika.length;
        if (priezviskoZakaznika.length > MAX_VELKOST_PRIEZVISKA) {
            throw new IllegalArgumentException("Priezvisko môže mať maximálne " + MAX_VELKOST_PRIEZVISKA + " znakov.");
        }
        this.priezviskoZakaznika = new char[MAX_VELKOST_PRIEZVISKA];
        System.arraycopy(priezviskoZakaznika,0,this.priezviskoZakaznika,0,priezviskoZakaznika.length);
        this.skutocnaDlzkaPriezviska = priezviskoZakaznika.length;
        this.idZakaznika = idZakaznika;
        if (ecv.length > MAX_VELKOST_ECV) {
            throw new IllegalArgumentException("ECV môže mať maximálne " + MAX_VELKOST_ECV + " znakov.");
        }
        this.ecv = new char[MAX_VELKOST_ECV];
        this.skutocnaDlzkaEcv = ecv.length;
        System.arraycopy(ecv, 0, this.ecv, 0, ecv.length);
        /*if (navstevyServisu.length > MAX_VELKOST_SERVISOV) {
            throw new IllegalArgumentException("Zákazník môže mať maximálne " + MAX_VELKOST_SERVISOV + " návštev servisu.");
        }*/
        this.navstevyServisu = new NavstevyServisu[MAX_VELKOST_SERVISOV];
    }

    public Vozidlo() {
        this.menoZakaznika = new char[MAX_VELKOST_MENA];
        this.priezviskoZakaznika = new char[MAX_VELKOST_PRIEZVISKA];
        this.idZakaznika = 0;
        this.ecv = new char[MAX_VELKOST_ECV];
        this.navstevyServisu = new NavstevyServisu[MAX_VELKOST_SERVISOV];
        this.skutocnaDlzkaMena = MAX_VELKOST_MENA;
        this.skutocnaDlzkaPriezviska = MAX_VELKOST_PRIEZVISKA;
        this.skutocnaDlzkaEcv = MAX_VELKOST_ECV; //TODO nedobre ak zapisujem pomocou bezparametrickeho konstruktora
    }

    @Override
    public String toString() {
        return "Vozidlo{" +
                "skutocnaDlzkaMena=" + skutocnaDlzkaMena +
                ", menoZakaznika=" + Arrays.toString(menoZakaznika) +
                ", skutocnaDlzkaPriezviska=" + skutocnaDlzkaPriezviska +
                ", priezviskoZakaznika=" + Arrays.toString(priezviskoZakaznika) +
                ", idZakaznika=" + idZakaznika +
                ", skutocnaDlzkaEcv=" + skutocnaDlzkaEcv +
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
            int dlzkaMena = dataInput.readInt();
            byte[] menoBytes = new byte[MAX_VELKOST_MENA];
            dataInput.readFully(menoBytes);
            this.menoZakaznika = new String(menoBytes, 0, dlzkaMena).toCharArray();

            int dlzkaPriezviska = dataInput.readInt();
            byte[] priezviskoBytes = new byte[MAX_VELKOST_PRIEZVISKA];
            dataInput.readFully(priezviskoBytes);
            this.priezviskoZakaznika = new String(priezviskoBytes, 0, dlzkaPriezviska).toCharArray();

            this.idZakaznika = dataInput.readInt();
            int dlzkaEcv = dataInput.readInt();
            byte[] ecvBytes = new byte[MAX_VELKOST_ECV];
            dataInput.readFully(ecvBytes);
            this.ecv = new String(ecvBytes,0,dlzkaEcv).toCharArray();

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
            dataOutput.writeInt(this.skutocnaDlzkaMena);
            dataOutput.write(String.format("%-" + MAX_VELKOST_MENA + "s", new String(menoZakaznika)).getBytes());
            dataOutput.writeInt(this.skutocnaDlzkaPriezviska);
            dataOutput.write(String.format("%-" + MAX_VELKOST_PRIEZVISKA + "s", new String(priezviskoZakaznika)).getBytes());
            dataOutput.writeInt(idZakaznika);
            dataOutput.writeInt(this.skutocnaDlzkaEcv);
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
        return Integer.BYTES + MAX_VELKOST_MENA + Integer.BYTES + MAX_VELKOST_PRIEZVISKA + Integer.BYTES + Integer.BYTES + MAX_VELKOST_ECV;// + MAX_VELKOST_SERVISOV * new NavstevyServisu().getSize();
    }
}
