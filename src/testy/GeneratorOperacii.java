package testy;

import US.NeutriedenySubor.Blok;
import US.NeutriedenySubor.NeutriedenySubor;
import rozhrania.IZaznam;
import triedy.Vozidlo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class GeneratorOperacii<T extends IZaznam<T>> {
    private final NeutriedenySubor<T> neutriedenySubor;
    private final int pocetOperacii;
    private Random random;
    private HashMap<Integer, ArrayList<IZaznam<T>>> bloky;
    private ArrayList<Integer> adresyBlokov;
    private int id;
    private int pocetVkladani;
    private int pocetMazani;
    private int pocetVyhladavani;
    private long seed;
    private final String[] MENA = {"Jozef", "Peter", "Marek", "Martin", "Michal", "Tomas", "Lukas", "Jakub", "Adam", "Filip"};
    private final String[] PRIEZVISKA = {"Roxer", "Solar", "Novy", "Kral", "Kralik", "Kralovic", "Kralovsky", "Molnar", "Kralov"};
    private final String[] ECV = {"SN467VH", "GL157LP", "BA123AB", "KE456CD", "PO789EF", "TT101HN", "BB202FB", "PP303ND", "KK404NS", "LL505ES"};

    public GeneratorOperacii(NeutriedenySubor<T> neutriedenySubor, int pocetOperacii) {
        this.neutriedenySubor = neutriedenySubor;
        this.pocetOperacii = pocetOperacii;
        this.seed = 28786858154200L;
        this.random = new Random(seed);
        this.bloky = new HashMap<>();
        this.adresyBlokov = new ArrayList<>();
        this.id = 1;
        this.pocetVkladani = 0;
        this.pocetMazani = 0;
        this.pocetVyhladavani = 0;
        generujOperacie();
    }

    private void generujOperacie() {
        zapisSeed();
        for (int i = 0; i < pocetOperacii; i++) {
            if (random.nextInt() < 0.7) {
                metodaVkladania();
                this.neutriedenySubor.vypisObsah();
            } else if (random.nextInt() < 0.15) {
                if (!bloky.isEmpty()) {
                    metodaMazania();
                    this.neutriedenySubor.vypisObsah();
                }
            } else {
                if (!bloky.isEmpty()) {
                    metodaVyhladavania();
                    this.neutriedenySubor.vypisObsah();
                }
            }
        }
        System.out.println("Počet vkladaní: " + pocetVkladani);
        System.out.println("Počet mazaní: " + pocetMazani);
        System.out.println("Počet vyhľadávaní: " + pocetVyhladavani);
        this.neutriedenySubor.vypisObsah();
    }

    private void zapisSeed() {
        File file = new File("seed.txt");
        try {
            file.createNewFile();
            java.io.FileWriter writer = new java.io.FileWriter(file);
            writer.write(String.valueOf(seed));
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private IZaznam<T> vytvorZaznam() {
        char[] meno = MENA[random.nextInt(MENA.length)].toCharArray();
        char[] priezvisko = PRIEZVISKA[random.nextInt(PRIEZVISKA.length)].toCharArray();
        char[] ecv = ECV[random.nextInt(ECV.length)].toCharArray();
        Vozidlo vozidlo = new Vozidlo(meno, priezvisko, id, ecv);
        id++;
        return (IZaznam<T>) vozidlo;
    }

    private void metodaVkladania() {
        IZaznam<T> zaznam = vytvorZaznam();
        int adresa = neutriedenySubor.vlozZaznam(zaznam);
        if (bloky.containsKey(adresa)) {
            bloky.get(adresa).add(zaznam);
        } else {
            ArrayList<IZaznam<T>> zaznamy = new ArrayList<>();
            zaznamy.add(zaznam);
            bloky.put(adresa, zaznamy);
            adresyBlokov.add(adresa);
        }
        System.out.println("Vložený záznam: " + zaznam + " do bloku: " + adresa);
        pocetVkladani++;
    }

    private void metodaMazania() {
        int adresa = adresyBlokov.get(random.nextInt(adresyBlokov.size()));
        ArrayList<IZaznam<T>> zaznamy = bloky.get(adresa);
        if (zaznamy.isEmpty()) {
            return;
        }
        IZaznam<T> zaznam = zaznamy.get(random.nextInt(zaznamy.size()));
        neutriedenySubor.zmazZaznam((T) zaznam, adresa);
        zaznamy.remove(zaznam);
        System.out.println("Zmazaný záznam: " + zaznam + " z bloku: " + adresa);
        pocetMazani++;
    }

    private void metodaVyhladavania() {
        int adresa = adresyBlokov.get(random.nextInt(adresyBlokov.size()));
        ArrayList<IZaznam<T>> zaznamy = bloky.get(adresa);
        if (zaznamy.isEmpty()) {
            return;
        }
        IZaznam<T> zaznam = zaznamy.get(random.nextInt(zaznamy.size()));
        neutriedenySubor.getZaznam((T) zaznam, adresa);
        System.out.println("Nájdený záznam: " + zaznam + " v bloku: " + adresa);
        pocetVyhladavani++;
    }
}
