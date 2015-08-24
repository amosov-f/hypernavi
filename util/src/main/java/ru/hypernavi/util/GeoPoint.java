package ru.hypernavi.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: amosov-f
 * Date: 26.11.14
 * Time: 23:33
 */
public final class GeoPoint {
    private static final double EARTH_RADIUS = 6371.0d;
    private final double latitude;
    private final double longitude;

    public GeoPoint(final double longitude, final double latitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public static double distance(@NotNull final GeoPoint a, @NotNull final GeoPoint b) {
        final double aLatitude = Math.toRadians(a.getLatitude());
        final double bLatitude = Math.toRadians(b.getLatitude());

        final double aLongitude = Math.toRadians(a.getLongitude());
        final double bLongitude = Math.toRadians(b.getLongitude());

        //noinspection MagicNumber
        final double answer = Math.pow(Math.sin((aLatitude - bLatitude) / 2), 2) + Math.cos(aLatitude) * Math.cos(bLatitude)
                      * Math.pow(Math.sin((aLongitude - bLongitude) / 2), 2);
        return 2 * EARTH_RADIUS * Math.asin(Math.sqrt(answer));
    }

    @Nullable
    public static GeoPoint parseString(final String point) {
        final String[] coordinates = point.split("[ ]");
        if (coordinates.length < 2) {
            return null;
        }
        final GeoPoint result;
        try {
            result = new GeoPoint(Double.parseDouble(coordinates[0]), Double.parseDouble(coordinates[1]));
        } catch (NumberFormatException ignored) {
            return null;
        }
        return result;
    }

    @NotNull
    @Override
    public String toString() {
        return "GeoPoint(" + longitude + ", " + latitude + ")";
    }
}
