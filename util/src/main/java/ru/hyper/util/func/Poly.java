package ru.hyper.util.func;

import org.apache.commons.math3.analysis.BivariateFunction;
import org.jetbrains.annotations.NotNull;

/**
 * User: amosov-f
 * Date: 27.11.14
 * Time: 1:52
 */
public class Poly implements BivariateFunction {
    @NotNull
    private double[] coefficients;

    public Poly(@NotNull final double[] coefficients) {
        this.coefficients = coefficients;
    }

    @Override
    public double value(final double x, final double y) {
        double value = 0;
        final int deg = deg();
        for (int k = 0, i = 0; i <= deg; i++) {
            for (int j = 0; j <= deg - i; j++) {
                value += coefficients[k++] * Math.pow(x, i) * Math.pow(y, j);
            }
        }
        return value;
    }

    public int deg() {
        return (int) Math.floor(Math.sqrt(2 * coefficients.length)) - 1;
    }
}
