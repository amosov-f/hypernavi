package ru.hypernavi.ml.regression;

import org.jetbrains.annotations.NotNull;

import java.util.function.DoubleUnaryOperator;


import org.junit.Assert;
import org.junit.Test;
import weka.core.Instance;
import weka.core.Instances;

/**
 * User: amosov-f
 * Date: 20.04.16
 * Time: 22:51
 */
public final class BestPolynomialRegressionTest extends RegressionTestBase {
    private static final double EPS = 1e-4;

    @Test
    public void test() throws Exception {
        test(x -> 3 * x + 2, 1);
        test(x -> x * x - 2 * x + 1, 2);
        test(x -> 4 * x * x * x - 3 * x * x + 2 * x - 1, 3);
    }

    private void test(@NotNull final DoubleUnaryOperator f, final int expectedDeg) throws Exception {
        final Instances dataset = dataset(f, 0, 10, 1);

        // building regression model
        final BestPolynomialRegression poly = new BestPolynomialRegression();
        poly.buildClassifier(dataset);

        // testing regression model
        final double x = 11;
        final Instance instance = instance(x, dataset);

        Assert.assertEquals(expectedDeg, poly.getDegree());
        Assert.assertEquals(f.applyAsDouble(x), poly.classifyInstance(instance), EPS);
    }
}