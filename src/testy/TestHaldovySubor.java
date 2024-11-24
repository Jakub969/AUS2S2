package testy;

import US.NeutriedenySubor.NeutriedenySubor;
import triedy.Osoba;

public class TestHaldovySubor {

    public TestHaldovySubor() {
        NeutriedenySubor<Osoba> osobaNeutriedenySubor = new NeutriedenySubor<>("osoba.bin", Osoba.class, 2);
        Osoba osoba1 = new Osoba("Janko".toCharArray(), "Hrasko".toCharArray(), 25);
        Osoba osoba2 = new Osoba("Ferko".toCharArray(),  "Mrkvicka".toCharArray(), 30);
        Osoba osoba3 = new Osoba("Jozko".toCharArray(), "Vajda".toCharArray(), 35);
        osobaNeutriedenySubor.vlozZaznam(osoba1);
        osobaNeutriedenySubor.vlozZaznam(osoba2);
        osobaNeutriedenySubor.vlozZaznam(osoba3);
        System.out.println("Obsah neutrideneho suboru:");
        osobaNeutriedenySubor.vypisObsah();

        Osoba hladanaOsoba = osobaNeutriedenySubor.getZaznam(new Osoba("".toCharArray(), "".toCharArray(), 30), 0);
        System.out.println("Hladana osoba: " + hladanaOsoba);

        hladanaOsoba = osobaNeutriedenySubor.getZaznam(new Osoba("".toCharArray(), "".toCharArray(), 35), 1);
        System.out.println("Hladana osoba: " + hladanaOsoba);

        Osoba mazanaOsoba = osobaNeutriedenySubor.zmazZaznam(new Osoba("".toCharArray(), "".toCharArray(), 35), 1);
        System.out.println("Zmazana osoba: " + mazanaOsoba);
        osobaNeutriedenySubor.ulozAktualnyBlok();
        System.out.println("Obsah neutrideneho suboru po zmazani:");
        osobaNeutriedenySubor.vypisObsah();

    }
}
