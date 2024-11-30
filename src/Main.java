import US.NeutriedenySubor.NeutriedenySubor;
import testy.GeneratorOperacii;
import triedy.Vozidlo;

public class Main {
    public static void main(String[] args) {
        NeutriedenySubor<Vozidlo> vozidlaNeutriedenySubor = new NeutriedenySubor<>("vozidlo.bin", Vozidlo.class, 134);
        new GeneratorOperacii<>(vozidlaNeutriedenySubor, 10);
    }
}
