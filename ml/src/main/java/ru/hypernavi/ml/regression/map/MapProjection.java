package ru.hypernavi.ml.regression.map;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.function.Function;


import ru.hypernavi.util.GeoPoint;

/**
 * Created by amosov-f on 22.01.16.
 */
public interface MapProjection extends Function<GeoPoint, Point> {
    @NotNull
    Point map(@NotNull final GeoPoint geoPoint);

    @NotNull
    @Override
    default Point apply(@NotNull final GeoPoint geoPoint) {
        return map(geoPoint);
    }
}
