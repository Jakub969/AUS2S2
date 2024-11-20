package testy;

import US.NeutriedenySubor.HaldovySubor;
import triedy.Osoba;

public class TestHaldovySubor {

    public TestHaldovySubor() {
        HaldovySubor<Osoba> osobaHaldovySubor = new HaldovySubor<>("osoba.bin", Osoba.class, 10);
        Osoba osoba1 = new Osoba("Janko", "Hrasko", 25);
        Osoba osoba2 = new Osoba("Ferko", "Mrkvicka", 30);
        Osoba osoba3 = new Osoba("Jozko", "Vajda", 35);
        osobaHaldovySubor.vlozZaznam(osoba1);
        osobaHaldovySubor.vlozZaznam(osoba2);
        osobaHaldovySubor.vlozZaznam(osoba3);
        System.out.println("Obsah haldoveho suboru:");
        osobaHaldovySubor.vypisObsah();

        Osoba hladanaOsoba = osobaHaldovySubor.getZaznam(new Osoba("", "", 2), 0);
        System.out.println("Hladana osoba: " + hladanaOsoba);

        osobaHaldovySubor.zmazZaznam(new Osoba("", "", 4), 0);
        System.out.println("Obsah haldoveho suboru po zmazani:");
        osobaHaldovySubor.vypisObsah();
    }
}
