package ru.hypernavi.ml.regression;

import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;
import weka.core.Instance;
import weka.core.Instances;

import java.util.function.DoubleUnaryOperator;

/**
 * User: amosov-f
 * Date: 05.07.15
 * Time: 1:37
 */
public final class PolynomialRegressionTest extends RegressionTestBase {
    private static final double EPS = 0.02;

    @Test
    public void test() throws Exception {
        test(x -> 3 * x + 2, 1);
        test(x -> x * x - 2 * x + 1, 2);
    }

    private void test(@NotNull final DoubleUnaryOperator f, final int deg) throws Exception {
        final Instances dataset = dataset(f, 0, 10, 1);

        // building regression model
        final PolynomialRegression poly = new PolynomialRegression(deg);
        poly.buildClassifier(dataset);

        // testing regression model
        final double x = 11;
        final Instance instance = instance(x, dataset);

        Assert.assertEquals(f.applyAsDouble(x), poly.classifyInstance(instance), EPS);
    }
}