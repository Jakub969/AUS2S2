package testy;

import US.NeutriedenySubor.Blok;
import US.NeutriedenySubor.NeutriedenySubor;
import rozhrania.IZaznam;
import triedy.Vozidlo;

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

    public GeneratorOperacii(NeutriedenySubor<T> neutriedenySubor, int pocetOperacii) {
        this.neutriedenySubor = neutriedenySubor;
        this.pocetOperacii = pocetOperacii;
        this.seed = System.nanoTime();
        this.random = new Random(seed);
        this.bloky = new HashMap<>();
        this.adresyBlokov = new ArrayList<>();
        this.id = 0;
        this.pocetVkladani = 0;
        this.pocetMazani = 0;
        this.pocetVyhladavani = 0;
        generujOperacie();
    }

    private void generujOperacie() {
        for (int i = 0; i < pocetOperacii; i++) {
            if (random.nextInt() < 0.7) {
                metodaVkladania();
            } else if (random.nextInt() < 0.15) {
                if (!bloky.isEmpty()) {
                    metodaMazania();
                }
            } else {
                if (!bloky.isEmpty()) {
                    metodaVyhladavania();
                }
            }
        }
        System.out.println("Počet vkladaní: " + pocetVkladani);
        System.out.println("Počet mazaní: " + pocetMazani);
        System.out.println("Počet vyhľadávaní: " + pocetVyhladavani);
        this.neutriedenySubor.vypisObsah();
        System.out.println("Seed: " + seed);
    }

    private IZaznam<T> vytvorZaznam() {
        char[] meno = vygenerujPoleznakov(15);
        char[] priezvisko = vygenerujPoleznakov(20);
        char[] ecv = vygenerujPoleznakov(10);
        Vozidlo vozidlo = new Vozidlo(meno, priezvisko, id, ecv);
        id++;
        return (IZaznam<T>) vozidlo;
    }

    private char[] vygenerujPoleznakov(int pocetZnakov) {
        char[] pole = new char[pocetZnakov];
        for (int i = 0; i < pocetZnakov; i++) {
            pole[i] = (char) (random.nextInt(26) + 'a');
        }
        return pole;
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
        IZaznam<T> zaznam = zaznamy.get(random.nextInt(zaznamy.size()));
        neutriedenySubor.zmazZaznam((T) zaznam, adresa);
        zaznamy.remove(zaznam);
        System.out.println("Zmazaný záznam: " + zaznam + " z bloku: " + adresa);
        pocetMazani++;
    }

    private void metodaVyhladavania() {
        int adresa = adresyBlokov.get(random.nextInt(adresyBlokov.size()));
        ArrayList<IZaznam<T>> zaznamy = bloky.get(adresa);
        IZaznam<T> zaznam = zaznamy.get(random.nextInt(zaznamy.size()));
        neutriedenySubor.getZaznam((T) zaznam, adresa);
        System.out.println("Nájdený záznam: " + zaznam + " v bloku: " + adresa);
        pocetVyhladavani++;
    }
}
