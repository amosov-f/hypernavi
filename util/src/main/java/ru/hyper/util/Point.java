package ru.hyper.util;

import org.jetbrains.annotations.NotNull;

/**
 * User: amosov-f
 * Date: 26.11.14
 * Time: 23:54
 */
public class Point {
    @NotNull
    private final GeoPoint geoPoint;
    @NotNull
    private final java.awt.Point imagePoint;

    public Point(@NotNull final GeoPoint geoPoint, @NotNull final java.awt.Point imagePoint) {
        this.geoPoint = geoPoint;
        this.imagePoint = imagePoint;
    }
}
