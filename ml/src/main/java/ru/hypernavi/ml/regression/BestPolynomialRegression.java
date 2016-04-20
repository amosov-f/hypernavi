package ru.hypernavi.ml.regression;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Objects;
import java.util.Random;
import java.util.stream.IntStream;


import weka.classifiers.AbstractClassifier;
import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;

/**
 * User: amosov-f
 * Date: 20.04.16
 * Time: 22:35
 */
public final class BestPolynomialRegression extends AbstractClassifier {
    private static final int MAX_DEG = 10;

    @Nullable
    private PolynomialRegression delegate;

    public int getDegree() {
        return getDelegate().getDegree();
    }

    @Override
    public void buildClassifier(@NotNull final Instances data) throws Exception {
        final int bestDeg = IntStream.range(0, MAX_DEG + 1).mapToObj(deg -> deg).max(Comparator.comparing(deg -> {
            try {
                return evaluate(deg, data);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        })).get();
        delegate = new PolynomialRegression(bestDeg);
        delegate.buildClassifier(data);
    }

    private double evaluate(final int deg, @NotNull final Instances data) throws Exception {
        final PolynomialRegression poly = new PolynomialRegression(deg);
        final Evaluation eval = new Evaluation(data);
        eval.crossValidateModel(poly, data, data.size(), new Random(0));
        return eval.correlationCoefficient();
    }

    @Override
    public double classifyInstance(@NotNull final Instance instance) throws Exception {
        return getDelegate().classifyInstance(instance);
    }

    @NotNull
    private PolynomialRegression getDelegate() {
        return Objects.requireNonNull(delegate, "Build classifier before!");
    }
}
