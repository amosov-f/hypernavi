package ru.hyper.util.ml;


import org.junit.Test;
import ru.hyper.util.func.Poly;

public class PolyFitterTest {
    @Test
    public void test() throws Exception {
        final PolyFitter fitter = new PolyFitter();
        for (int x = 0; x < 100; x++) {
            fitter.add(x, 0, x * x);
        }
        final Poly poly = fitter.fit();
        System.out.println(poly);
    }
}