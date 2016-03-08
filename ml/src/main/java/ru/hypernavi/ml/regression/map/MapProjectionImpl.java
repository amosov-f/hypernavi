package ru.hypernavi.ml.regression.map;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Arrays;
import java.util.function.ToDoubleFunction;


import ru.hypernavi.commons.PointMap;
import ru.hypernavi.ml.factor.Factor;
import ru.hypernavi.ml.regression.PolynomialRegression;
import ru.hypernavi.ml.regression.WekaRegression;
import ru.hypernavi.util.GeoPoint;

/**
 * Created by amosov-f on 22.01.16.
 */
public final class MapProjectionImpl implements MapProjection {
    public static final Factor<PointMap> LONGITUDE = new Factor.Lambda<>("longitude", p -> p.getGeoPoint().getLongitude());
    public static final Factor<PointMap> LATITUDE = new Factor.Lambda<>("latitude", p -> p.getGeoPoint().getLatitude());
    public static final Factor<PointMap> X = new Factor.Lambda<>("x", p -> p.getMapPoint().getX());
    public static final Factor<PointMap> Y = new Factor.Lambda<>("y", p -> p.getMapPoint().getY());

    @NotNull
    private final ToDoubleFunction<GeoPoint> mapX;
    @NotNull
    private final ToDoubleFunction<GeoPoint> mapY;

    public MapProjectionImpl(@NotNull final ToDoubleFunction<GeoPoint> mapX, @NotNull final ToDoubleFunction<GeoPoint> mapY) {
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
        final java.util.List<? extends Factor<PointMap>> features = Arrays.asList(LONGITUDE, LATITUDE);
        // TODO: polynom degree
        final WekaRegression<PointMap> fx = new WekaRegression<>(new PolynomialRegression(1), features, X);
        fx.learn(points);
        final WekaRegression<PointMap> fy = new WekaRegression<>(new PolynomialRegression(1), features, Y);
        fy.learn(points);
        return new MapProjectionImpl(projection(fx), projection(fy));
    }

    @NotNull
    private static ToDoubleFunction<GeoPoint> projection(@NotNull final ToDoubleFunction<PointMap> f) {
        return geoPoint -> f.applyAsDouble(PointMap.of(geoPoint));
    }
}