package ru.hypernavi.ml.regression;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.hypernavi.ml.util.ModelEvaluation;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.evaluation.Evaluation;
import weka.core.Instance;
import weka.core.Instances;

import java.util.Comparator;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * User: amosov-f
 * Date: 20.04.16
 * Time: 22:35
 */
public final class BestPolynomialRegression extends AbstractClassifier {
    private static final Log LOG = LogFactory.getLog(BestPolynomialRegression.class);

    private static final int MAX_DEG = 5;

    @Nullable
    private PolynomialRegression delegate;
    @Nullable
    private Evaluation bestDegreeEvaluation;

    public int getDegree() {
        return getDelegate().getDegree();
    }

    @Override
    public void buildClassifier(@NotNull final Instances data) throws Exception {
        final ModelEvaluation<PolynomialRegression> bestPoly = IntStream.rangeClosed(0, MAX_DEG)
            .mapToObj(deg -> evaluate(deg, data))
            .max(Comparator.comparing(ModelEvaluation::getCorrelationCoefficient))
            .get();
        final int bestDeg = bestPoly.getClassifier().getDegree();
        LOG.debug("Best degree of polynom is " + bestDeg);
        delegate = new PolynomialRegression(bestDeg);
        delegate.buildClassifier(data);
//        System.out.println(delegate);
        bestDegreeEvaluation = bestPoly.getEvaluation();
    }

    @NotNull
    private ModelEvaluation<PolynomialRegression> evaluate(final int deg, @NotNull final Instances data) {
        return ModelEvaluation.leaveOneOut(new PolynomialRegression(deg), data);
    }

    @Override
    public double classifyInstance(@NotNull final Instance instance) throws Exception {
        return getDelegate().classifyInstance(instance);
    }

    @NotNull
    private PolynomialRegression getDelegate() {
        return Objects.requireNonNull(delegate, "Build classifier before!");
    }

    @NotNull
    public Evaluation getBestDegreeEvaluation() {
        return Objects.requireNonNull(bestDegreeEvaluation, "Build classifier before!");
    }
}
