import US.NeutriedenySubor.NeutriedenySubor;
import testy.GeneratorOperacii;
import triedy.Vozidlo;

public class Main {
    public static void main(String[] args) {
        NeutriedenySubor vozidlaNeutriedenySubor = new NeutriedenySubor("vozidlo.bin", Vozidlo.class, 4096);
        GeneratorOperacii generatorOperacii = new GeneratorOperacii(vozidlaNeutriedenySubor, 10);
    }
}
