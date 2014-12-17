package ru.hyper.core.model;

import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoint;
import org.apache.commons.math3.genetics.Fitness;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.EnumMap;
import java.util.List;

/**
 * User: amosov-f
 * Date: 26.11.14
 * Time: 23:35
 */
public class Scheme {
    @NotNull
    private final Image image;
    @NotNull
    private final EnumMap<Category, List<Point2D.Double>> markup;

    public Scheme(@NotNull final Image image, @NotNull final EnumMap<Category, List<Point2D.Double>> markup) {
        this.image = image;
        this.markup = markup;
    }
}
