package ru.hypernavi.ml.regression;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;

import java.util.Comparator;
import java.util.Objects;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * User: amosov-f
 * Date: 20.04.16
 * Time: 22:35
 */
public final class BestPolynomialRegression extends AbstractClassifier {
    private static final Log LOG = LogFactory.getLog(BestPolynomialRegression.class);

    private static final int MAX_DEG = 7;

    @Nullable
    private PolynomialRegression delegate;

    public int getDegree() {
        return getDelegate().getDegree();
    }

    @Override
    public void buildClassifier(@NotNull final Instances data) throws Exception {
        final int bestDeg = IntStream.rangeClosed(0, MAX_DEG)
            .boxed()
            .max(Comparator.comparing(deg -> evaluate(deg, data)))
            .orElse(1);
        LOG.debug("Best degree of polynom is " + bestDeg);
        delegate = new PolynomialRegression(bestDeg);
        delegate.buildClassifier(data);
    }

    private double evaluate(final int deg, @NotNull final Instances data) {
        try {
            return evaluateImpl(deg, data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private double evaluateImpl(final int deg, @NotNull final Instances data) throws Exception {
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
