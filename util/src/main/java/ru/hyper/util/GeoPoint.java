package ru.hyper.util;

import org.jetbrains.annotations.NotNull;

/**
 * User: amosov-f
 * Date: 26.11.14
 * Time: 23:33
 */
public class GeoPoint {
    private final double latitude;
    private final double longitude;


    public GeoPoint(final double latitude, final double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @NotNull
    @Override
    public String toString() {
        return "GeoPoint(" + latitude + ", " + longitude +")";
    }
}
