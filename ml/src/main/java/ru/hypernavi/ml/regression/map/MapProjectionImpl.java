package ru.hypernavi.ml.regression.map;

import org.jetbrains.annotations.NotNull;
import ru.hypernavi.commons.PointMap;
import ru.hypernavi.ml.factor.Factor;
import ru.hypernavi.ml.regression.BestPolynomialRegression;
import ru.hypernavi.ml.regression.WekaRegression;
import ru.hypernavi.util.GeoPoint;
import ru.hypernavi.util.function.MoreFunctions;
import weka.classifiers.evaluation.Evaluation;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

/**
 * Created by amosov-f on 22.01.16.
 */
public final class MapProjectionImpl implements MapProjection {
    public static final Factor<PointMap> LONGITUDE = new Factor.Lambda<>("longitude", p -> p.getGeoPoint().getLongitude());
    public static final Factor<PointMap> LATITUDE = new Factor.Lambda<>("latitude", p -> p.getGeoPoint().getLatitude());
    private static final List<? extends Factor<PointMap>> FEATURES = Arrays.asList(LONGITUDE, LATITUDE);

    public static final Factor<PointMap> X = new Factor.Lambda<>("x", p -> p.getMapPoint().getX());
    public static final Factor<PointMap> Y = new Factor.Lambda<>("y", p -> p.getMapPoint().getY());

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
    public static MapProjection learn(@NotNull final PointMap... points) {
        final WekaRegression<PointMap> fx = learn(X, points);
        final WekaRegression<PointMap> fy = learn(Y, points);
        return new MapProjectionImpl(projection(fx), projection(fy));
    }

    @NotNull
    public static Point map(@NotNull final GeoPoint geoPoint, @NotNull final PointMap... points) {
        return learn(points).map(geoPoint);
    }

    @NotNull
    public static ValidationResult validate(@NotNull final PointMap... points) {
        final Evaluation ex = learn(X, points).crossValidate();
        final Evaluation ey = learn(Y, points).crossValidate();

        final Map<Integer, Integer> fx = predictions(ex);
        final Map<Integer, Integer> fy = predictions(ey);
        final Point[] diffs = Arrays.stream(points)
                .map(PointMap::getMapPoint)
                .map(p -> diff(p, new Point(fx.get(p.x), fy.get(p.y))))
                .toArray(Point[]::new);
        return new ValidationResult(diffs, ex.toSummaryString().trim(), ey.toSummaryString().trim());
    }

    @NotNull
    private static Map<Integer, Integer> predictions(@NotNull final Evaluation evaluation) {
        return evaluation.predictions()
                .stream()
                .collect(Collectors.toMap(p -> (int) p.actual(), p -> (int) p.predicted(), MoreFunctions.rightProjection()));
    }

    @NotNull
    private static WekaRegression<PointMap> learn(@NotNull final Factor<PointMap> answer,
                                                  @NotNull final PointMap... points)
    {
        final WekaRegression<PointMap> f = new WekaRegression<>(new BestPolynomialRegression(), FEATURES, answer);
        f.learn(points);
        return f;
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
