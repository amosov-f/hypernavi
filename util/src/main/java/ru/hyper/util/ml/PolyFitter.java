package ru.hyper.util.ml;

import com.google.common.primitives.Doubles;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import org.jetbrains.annotations.NotNull;
import ru.hyper.util.func.Poly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: amosov-f
 * Date: 27.11.14
 * Time: 2:02
 */
public class PolyFitter {
    private static final double LEARN_PART = 0.8;

    @NotNull
    private final List<PointValuePair> points = new ArrayList<>();

    @NotNull
    public Poly fit() {
        final List<PointValuePair> learn = new ArrayList<>();
        final List<PointValuePair> test = new ArrayList<>();
        for (final PointValuePair p : points) {
            if (Math.random() < LEARN_PART) {
                learn.add(p);
            } else {
                test.add(p);
            }
        }
        double prevTarget = Double.MAX_VALUE;
        for (int deg = 1; ; deg++) {
            final Poly poly = fit(deg, learn);
            double curTarget = target(poly, test);
            if (curTarget > prevTarget) {
                return fit(deg - 1, points);
            }
            prevTarget = curTarget;
        }
    }

    public void add(final double x, final double y, final double value) {
        points.add(new PointValuePair(new double[]{x, y}, value));
    }

    @NotNull
    private double[][] toMatrix(@NotNull final List<List<Double>> list) {
        final double[][] matrix = new double[list.size()][list.get(0).size()];
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.get(i).size(); j++) {
                matrix[i][j] = list.get(i).get(j);
            }
        }
        return matrix;
    }

    @NotNull
    private Poly fit(final int deg, @NotNull final List<PointValuePair> points) {
        final List<List<Double>> x = new ArrayList<>();
        final List<Double> y = new ArrayList<>();
        for (final PointValuePair p : points) {
            final List<Double> row = new ArrayList<>();
            for (int i = 0; i <= deg; i++) {
                for (int j = 0; j <= deg - i; j++) {
                    row.add(Math.pow(p.getPoint()[0], i) * Math.pow(p.getPoint()[1], j));
                }
            }
            x.add(row);
            y.add(p.getValue());
        }
        final OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
        regression.setNoIntercept(true);
        System.out.println(Arrays.deepToString(toMatrix(x)));
        regression.newSampleData(Doubles.toArray(y), toMatrix(x));
        return new Poly(regression.estimateRegressionParameters());
    }

    private double target(@NotNull final Poly poly, @NotNull final List<PointValuePair> points) {
        double target = 0;
        for (final PointValuePair p : points) {
            target += Math.pow(poly.value(p.getPoint()[0], p.getPoint()[1]) - p.getValue(), 2);
        }
        return target;
    }
}
