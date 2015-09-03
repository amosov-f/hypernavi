package ru.hypernavi.ml;

import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;
import ru.hypernavi.ml.regression.PolynomialRegression;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.function.DoubleUnaryOperator;

/**
 * User: amosov-f
 * Date: 05.07.15
 * Time: 1:37
 */
public final class PolynomialRegressionTest {
    private static final double EPS = 1e-6;

    @Test
    public void test() throws Exception {
        test(x -> 3 * x + 2, 1);
        test(x -> x * x - 2 * x + 1, 2);
    }

    private void test(@NotNull final DoubleUnaryOperator f, final int deg) throws Exception {
        // creating regression attributes
        final ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("x"));
        attributes.add(new Attribute("y"));
        // creating dataset
        final Instances instances = new Instances("dataset", attributes, 10);
        instances.setClassIndex(1);
        for (double x = 0; x < 10; x++) {
            final Instance instance = new DenseInstance(2);
            instance.setValue(0, x);
            instance.setValue(1, f.applyAsDouble(x));
            instances.add(instance);
        }

        // building regression model
        final PolynomialRegression poly = new PolynomialRegression(deg);
        poly.buildClassifier(instances);

        // testing regression model
        final Instance instance = new DenseInstance(2);
        final double x = 10;
        instance.setValue(0, x);
        instance.setDataset(instances);
        Assert.assertEquals(f.applyAsDouble(x), poly.classifyInstance(instance), EPS);
    }
}