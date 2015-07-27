package ru.hypernavi.util;

import org.jetbrains.annotations.NotNull;

/**
 * User: amosov-f
 * Date: 26.11.14
 * Time: 23:33
 */
public final class GeoPoint {
    private final double latitude;
    private final double longitude;
    private static final double EARTH_RADIUS = 6350;

    public GeoPoint(final double latitude, final double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() { return latitude; }
    public double getLongitude() {
        return longitude;
    }

    public static double distance(GeoPoint a, GeoPoint b) {
        double lat = a.getLatitude() - b.getLatitude();
        double lon = a.getLongitude() - b.getLongitude();
        double angleDistance = Math.sqrt(lat * lat + lon * lon);
        return EARTH_RADIUS * 360 / 2 * Math.PI * angleDistance;
    }

    @NotNull
    @Override
    public String toString() {
        return "GeoPoint(" + latitude + ", " + longitude +")";
    }
}
