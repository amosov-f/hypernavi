package ru.hypernavi.util;

import org.jetbrains.annotations.NotNull;

/**
 * User: amosov-f
 * Date: 26.11.14
 * Time: 23:33
 */
public final class GeoPoint {
    private static final double EARTH_RADIUS = 6371.0d;
    private final double latitude;
    private final double longitude;

    public GeoPoint(final double latitude, final double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() { return latitude; }
    public double getLongitude() {
        return longitude;
    }

    public static double distance(@NotNull final GeoPoint a, @NotNull final GeoPoint b) {
        final double aLatitude = Math.toRadians(a.getLatitude());
        final double bLatitude = Math.toRadians(b.getLatitude());

        final double aLongitude = Math.toRadians(a.getLongitude());
        final double bLongitude = Math.toRadians(b.getLongitude());

        final double answer = Math.pow(Math.sin((aLatitude - bLatitude) / 2.0d), 2.0d) + Math.cos(aLatitude) * Math.cos(bLatitude)
                      * Math.pow(Math.sin((aLongitude - bLongitude) / 2.0d), 2.0d);
        return 2.0d * EARTH_RADIUS * Math.asin(Math.sqrt(answer));
    }

    @NotNull
    @Override
    public String toString() {
        return "GeoPoint(" + latitude + ", " + longitude +")";
    }
}
