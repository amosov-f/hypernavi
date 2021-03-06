package ru.hypernavi.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * User: amosov-f
 * Date: 26.11.14
 * Time: 23:33
 */
public final class GeoPointImpl implements GeoPoint {
    private static final Log LOG = LogFactory.getLog(GeoPointImpl.class);

    private static final double EARTH_RADIUS = 6371.0d;

    private final double longitude;
    private final double latitude;

    public GeoPointImpl(final double longitude, final double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    @Override
    public double getLongitude() {
        return longitude;
    }

    @Override
    public double getLatitude() {
        return latitude;
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
    public static GeoPointImpl parseString(final String point) {
        final String[] coordinates = point.split("[ ]");
        if (coordinates.length < 2) {
            return null;
        }
        final GeoPointImpl result;
        try {
            result = new GeoPointImpl(Double.parseDouble(coordinates[0]), Double.parseDouble(coordinates[1]));
        } catch (NumberFormatException ignored) {
            return null;
        }
        return result;
    }

    @NotNull
    @Override
    public String toString() {
        return "(" + getLongitude() + ", " + getLatitude() + ")";
    }

    @Nullable
    public static GeoPointImpl construct(@NotNull final JSONObject obj) {
        try {
            return new GeoPointImpl(obj.getDouble("lon"), obj.getDouble("lat"));
        } catch (JSONException e) {
            LOG.info(e.getMessage());
            return null;
        }
    }


    public JSONObject toJSON() {
        final JSONObject obj = new JSONObject();
        try {
            obj.put("lon", getLongitude());
            obj.put("lat", getLatitude());
        } catch (JSONException ignored) {
            return null;
        }
        return obj;
    }
}
