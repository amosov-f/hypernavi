package ru.hypernavi.commons;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Objects;


import ru.hypernavi.util.GeoPoint;

/**
 * Created by amosov-f on 23.01.16.
 */
public final class PointMap {
    public static final PointMap[] EMPTY_ARRAY = new PointMap[0];

    @NotNull
    private final GeoPoint geoPoint;
    @Nullable
    private final Point mapPoint;

    private PointMap(@NotNull final GeoPoint geoPoint, @Nullable final Point mapPoint) {
        this.geoPoint = geoPoint;
        this.mapPoint = mapPoint;
    }

    @NotNull
    public static PointMap of(@NotNull final GeoPoint geoPoint) {
        return of(geoPoint, null);
    }

    @NotNull
    public static PointMap of(@NotNull final GeoPoint geoPoint, @Nullable final Point mapPoint) {
        return new PointMap(geoPoint, mapPoint);
    }

    @NotNull
    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    @NotNull
    public Point getMapPoint() {
        return Objects.requireNonNull(mapPoint);
    }
}
