package ru.hypernavi.ml.regression.map;

import org.jetbrains.annotations.NotNull;
import ru.hypernavi.commons.PointMap;
import ru.hypernavi.ml.factor.Factor;
import ru.hypernavi.ml.regression.BestPolynomialRegression;
import ru.hypernavi.ml.regression.WekaRegression;
import ru.hypernavi.ml.util.ModelEvaluation;
import ru.hypernavi.util.GeoPoint;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.evaluation.Prediction;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.function.ToDoubleFunction;

/**
 * Created by amosov-f on 22.01.16.
 */
public final class MapProjectionImpl implements MapProjection {
    @NotNull
    private final ToDoubleFunction<GeoPoint> mapX;
    @NotNull
    private final ToDoubleFunction<GeoPoint> mapY;

    private MapProjectionImpl(@NotNull final ToDoubleFunction<GeoPoint> mapX, @NotNull final ToDoubleFunction<GeoPoint> mapY) {
        this.mapX = mapX;
        this.mapY = mapY;
    }

    @NotNull
    @Override
    public Point map(@NotNull final GeoPoint geoPoint) {
        return new Point((int) mapX.applyAsDouble(geoPoint), (int) mapY.applyAsDouble(geoPoint));
    }

    @NotNull
    public static MapProjection learn(@NotNull final List<? extends Factor<PointMap>> features,
                                      @NotNull final PointMap... points)
    {
        final WekaRegression<PointMap> fx = learn(features, MapFactors.X, points).getClassifier();
        final WekaRegression<PointMap> fy = learn(features, MapFactors.Y, points).getClassifier();
        return new MapProjectionImpl(projection(fx), projection(fy));
    }

    @NotNull
    public static ValidationResult validate(@NotNull final List<? extends Factor<PointMap>> features,
                                            @NotNull final PointMap... points)
    {
        final Evaluation evalX = learn(features, MapFactors.X, points).getEvaluation();
        final Evaluation evalY = learn(features, MapFactors.Y, points).getEvaluation();
        final Point[] diffs = Arrays.stream(points)
                .map(PointMap::getMapPoint)
                .map(p -> diff(p, new Point(prediction(evalX, p.x), prediction(evalY, p.y))))
                .toArray(Point[]::new);
        return new ValidationResult(diffs, evalX, evalY);
    }

    private static int prediction(@NotNull final Evaluation evaluation, final int actual) {
        return (int) evaluation.predictions().stream()
                .filter(p -> (int) p.actual() == actual)
                .mapToDouble(Prediction::predicted)
                .findAny()
                .orElseThrow(() -> new IllegalStateException("No prediction for actual " + actual));
    }

    @NotNull
    public static ModelEvaluation<WekaRegression<PointMap>> learn(@NotNull final List<? extends Factor<PointMap>> features,
                                                                  @NotNull final Factor<PointMap> answer,
                                                                  @NotNull final PointMap... points)
    {
        final BestPolynomialRegression poly = new BestPolynomialRegression();
        final WekaRegression<PointMap> f = new WekaRegression<>(poly, features, answer);
        f.learn(points);
        return new ModelEvaluation<>(f, poly.getBestDegreeEvaluation());
    }

    @NotNull
    public static MapProjection deserialize(@NotNull final String xModel,
                                            @NotNull final String yModel,
                                            @NotNull final List<? extends Factor<PointMap>> features)
    {
        final WekaRegression<PointMap> fx = WekaRegression.deserialize(xModel, features, MapFactors.X);
        fx.init();
        final WekaRegression<PointMap> fy = WekaRegression.deserialize(yModel, features, MapFactors.Y);
        fy.init();
        return new MapProjectionImpl(projection(fx), projection(fy));
    }

    @NotNull
    private static Point diff(@NotNull final Point p1, @NotNull final Point p2) {
        return new Point(p2.x - p1.x, p2.y - p1.y);
    }

    @NotNull
    private static ToDoubleFunction<GeoPoint> projection(@NotNull final ToDoubleFunction<PointMap> f) {
        return geoPoint -> f.applyAsDouble(PointMap.of(geoPoint));
    }
}
