package triedy;

import rozhrania.IZaznam;

import java.io.*;
import java.util.Date;

public class NavstevyServisu implements IZaznam<NavstevyServisu> {
    private final int MAX_VELKOST_POPISU = 20;
    private final int MAX_POCET_POPISOV = 10;

    private Date datum; //da sa to previest na long
    private double cena;
    private char[][] popis;
    private int id_servisu;

    public NavstevyServisu(Date datum, double cena, char[][] popis, int id_servisu) {
        this.datum = datum;
        this.cena = cena;
        if (popis.length > MAX_POCET_POPISOV) {
            throw new IllegalArgumentException("Zákazník môže mať maximálne " + MAX_POCET_POPISOV + " popisov.");
        }

        for (char[] riadok : popis) {
            if (riadok.length > MAX_VELKOST_POPISU) {
                throw new IllegalArgumentException("Popis môže mať maximálne " + MAX_VELKOST_POPISU + " znakov.");
            }
        }
        this.popis = popis;
        this.id_servisu = id_servisu;
    }

    public NavstevyServisu() {
        this.datum = new Date();
        this.cena = 0.0;
        this.popis = new char[MAX_POCET_POPISOV][MAX_VELKOST_POPISU];
        this.id_servisu = 0;
    }

    @Override
    public boolean rovnaSa(NavstevyServisu objekt) {
        return this.id_servisu == objekt.id_servisu;
    }

    @Override
    public NavstevyServisu vytvorKopiu() {
        return new NavstevyServisu(datum, cena, popis, id_servisu);
    }

    @Override
    public NavstevyServisu fromByteArray(byte[] poleBytov) {
        ByteArrayInputStream in = new ByteArrayInputStream(poleBytov);
        DataInputStream dataInput = new DataInputStream(in);
        try {
            this.datum = new Date(dataInput.readLong());
            this.cena = dataInput.readDouble();
            int pocetPopisov = dataInput.readInt();
            this.popis = new char[pocetPopisov][];
            for (int i = 0; i < pocetPopisov; i++) {
                int dlzkaPopisu = dataInput.readInt();
                byte[] popisBytes = new byte[MAX_VELKOST_POPISU];
                dataInput.readFully(popisBytes);
                this.popis[i] = new String(popisBytes, 0, dlzkaPopisu).toCharArray();
            }
            this.id_servisu = dataInput.readInt();
            return this;
        } catch (Exception e) {
            throw new IllegalStateException("Chyba pri deserializácii záznamu.", e);
        }
    }

    @Override
    public byte[] toByteArray() {
        ByteArrayOutputStream hlpByteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream hlpOutStream = new DataOutputStream(hlpByteArrayOutputStream);
        try {
            hlpOutStream.writeLong(this.datum.getTime());
            hlpOutStream.writeDouble(this.cena);
            hlpOutStream.writeInt(this.popis.length);
            for (char[] riadok : this.popis) {
                int dlzkaPopisu = riadok.length;
                hlpOutStream.writeInt(dlzkaPopisu);
                byte[] popisBytes = new String(riadok).getBytes();
                hlpOutStream.write(popisBytes, 0, popisBytes.length);
            }
            hlpOutStream.writeInt(this.id_servisu);
            return hlpByteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Chyba pri serializácii záznamu.", e);
        }
    }

    @Override
    public int getSize() {
        return Long.BYTES + Double.BYTES + Integer.BYTES + (MAX_VELKOST_POPISU + 1) * popis.length + Integer.BYTES;
    }
}
