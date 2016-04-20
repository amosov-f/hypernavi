package ru.hypernavi.ml.regression;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.function.DoubleUnaryOperator;


import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 * User: amosov-f
 * Date: 20.04.16
 * Time: 22:53
 */
public class RegressionTestBase {
    @NotNull
    private final ArrayList<Attribute> attributes = new ArrayList<Attribute>() {{
        add(new Attribute("x"));
        add(new Attribute("y"));
    }};

    @NotNull
    protected Instances dataset(@NotNull final DoubleUnaryOperator f, final double x1, final double x2, final double dx) {
        // creating dataset
        final Instances instances = new Instances("dataset", attributes, (int) ((x2 - x1) / dx) + 1);
        instances.setClassIndex(1);
        for (double x = x1; x <= x2; x += dx) {
            final Instance instance = new DenseInstance(2);
            instance.setValue(0, x);
            instance.setValue(1, f.applyAsDouble(x));
            instances.add(instance);
        }
        return instances;
    }

    @NotNull
    protected Instance instance(final double x, @NotNull final Instances dataset) {
        final Instance instance = new DenseInstance(2);
        instance.setValue(0, x);
        instance.setDataset(dataset);
        return instance;
    }
}
