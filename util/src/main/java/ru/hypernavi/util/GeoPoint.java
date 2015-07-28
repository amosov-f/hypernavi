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
    private static final double EARTH_RADIUS = 6371.0d;
    private static final double GRAD_TO_RADIAN = 2.0d * Math.PI / 360.0d;

    public static class My3DPoint {
        private final double x;
        private final double y;
        private final double z;

        private My3DPoint(final double currentX, final double currentY, final double currentZ) {
            x = currentX;
            y = currentY;
            z = currentZ;
        }

        private My3DPoint(final GeoPoint p) {
            final double latitudeInRadian = p.getLatitude() * GRAD_TO_RADIAN;
            final double longitudeInRadian = p.getLongitude() *GRAD_TO_RADIAN;
            x = EARTH_RADIUS * Math.cos(latitudeInRadian) * Math.cos(longitudeInRadian);
            y = EARTH_RADIUS * Math.cos(latitudeInRadian) * Math.sin(longitudeInRadian);
            z = EARTH_RADIUS * Math.sin(latitudeInRadian);
        }
    }

    public GeoPoint(final double latitude, final double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() { return latitude; }
    public double getLongitude() {
        return longitude;
    }

    public static double distance(final GeoPoint a, final GeoPoint b) {

        final My3DPoint pA = new My3DPoint(a);
        final My3DPoint pB = new My3DPoint(b);
        double aBDistance = Math.pow(pA.x - pB.x, 2.0d) + Math.pow(pA.y - pB.y, 2.0d) +  Math.pow(pA.z - pB.z, 2.0d);
        aBDistance = Math.sqrt(aBDistance);
        final double cosPhi = 1.0d - aBDistance * aBDistance / (2.0d * EARTH_RADIUS * EARTH_RADIUS);
        final double phi = Math.acos(cosPhi);

        return EARTH_RADIUS * phi;
    }

    @NotNull
    @Override
    public String toString() {
        return "GeoPoint(" + latitude + ", " + longitude +")";
    }
}
