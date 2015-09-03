package ru.hypernavi.ml.regression;

import org.jetbrains.annotations.NotNull;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.functions.LinearRegression;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User: amosov-f
 * Date: 10.05.15
 * Time: 14:59
 */
public final class PolynomialRegression extends AbstractClassifier {
    private final int deg;
    @NotNull
    private final LinearRegression regression = new LinearRegression();

    private List<int[]> tuples;

    public PolynomialRegression(final int deg) {
        this.deg = deg;
    }

    @Override
    public void buildClassifier(@NotNull final Instances data) throws Exception {
        tuples = tuples(deg, data.numAttributes() - 1);
        tuples.remove(0); // remove [0, 0, ..., 0]
        regression.buildClassifier(instances(data));
    }

    @Override
    public double classifyInstance(@NotNull final Instance instance) throws Exception {
        return regression.classifyInstance(instance(instance));
    }

    @NotNull
    private Instances instances(@NotNull final Instances data) {
        final ArrayList<Attribute> attributes = new ArrayList<>();
        for (final int[] tuple : tuples) {
            final StringBuilder name = new StringBuilder();
            for (int i = 0; i < tuple.length; i++) {
                if (tuple[i] > 0) {
                    if (name.length() != 0) {
                        name.append(" * ");
                    }
                    name.append(data.attribute(i).name());
                }
                if (tuple[i] > 1) {
                    name.append("^").append(tuple[i]);
                }
            }
            attributes.add(new Attribute(name.toString()));
        }
        attributes.add(data.classAttribute());
        final Instances instances = new Instances(data.relationName(), attributes, data.size());
        instances.setClass(data.classAttribute());
        instances.addAll(data.stream().map(this::instance).collect(Collectors.toList()));
        return instances;
    }

    @NotNull
    private Instance instance(@NotNull final Instance x) {
        final Instance instance = new DenseInstance(tuples.size() + 1);
        for (int i = 0; i < tuples.size(); i++) {
            instance.setValue(i, value(x, tuples.get(i)));
        }
        instance.setValue(tuples.size(), x.classValue());
        return instance;
    }

    private static double value(@NotNull final Instance x, final int[] tuple) {
        double value = 1.0;
        for (int i = 0; i < tuple.length; i++) {
            value *= Math.pow(x.value(i), tuple[i]);
        }
        return value;
    }

    @NotNull
    private static List<int[]> tuples(final int deg, final int n) {
        final List<int[]> tuples = new ArrayList<>();
        dfs(tuples, new int[n], 0, deg);
        return tuples;
    }

    private static void dfs(@NotNull final List<int[]> tuples, @NotNull final int[] tuple, final int i, final int deg) {
        if (i == tuple.length) {
            tuples.add(tuple.clone());
            return;
        }
        for (int d = 0; d <= deg; d++) {
            tuple[i] = d;
            dfs(tuples, tuple, i + 1, deg - d);
        }
    }

    @NotNull
    @Override
    public String toString() {
        return "Polynomial Regression Model of " + deg + " degree, delegate:" + regression.toString();
    }
}
