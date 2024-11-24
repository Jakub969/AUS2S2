import US.NeutriedenySubor.NeutriedenySubor;
import testy.GeneratorOperacii;
import testy.TestHaldovySubor;
import triedy.Vozidlo;

public class Main {
    public static void main(String[] args) {
        //new TestHaldovySubor();
        NeutriedenySubor vozidlaNeutriedenySubor = new NeutriedenySubor("vozidlo.bin", Vozidlo.class, 2);
        GeneratorOperacii generatorOperacii = new GeneratorOperacii(vozidlaNeutriedenySubor, 10);
    }
}
