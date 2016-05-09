package ru.hypernavi.util;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;


import com.google.gson.*;
import ru.hypernavi.util.json.GsonUtils;

/**
 * Created by amosov-f on 20.12.15.
 */
public final class ArrayGeoPoint implements GeoPoint {
    static {
        GsonUtils.registerTypeAdapter(GeoPoint.class, new JsonSerializer<GeoPoint>() {
            @NotNull
            @Override
            public JsonElement serialize(@NotNull final GeoPoint geoPoint, @NotNull final Type t, @NotNull final JsonSerializationContext context) {
                return context.serialize(geoPoint);
            }
        });
        GsonUtils.registerTypeAdapter(GeoPoint.class, new JsonDeserializer<GeoPoint>() {
            @NotNull
            @Override
            public GeoPoint deserialize(@NotNull final JsonElement json, @NotNull final Type t, @NotNull final JsonDeserializationContext context) {
                return context.deserialize(json, ArrayGeoPoint.class);
            }
        });
    }

    @NotNull
    private final String type = "Point";
    @NotNull
    private final double[] coordinates;

    private ArrayGeoPoint(final double longitude, final double latitude) {
        this.coordinates = new double[]{longitude, latitude};
    }

    @NotNull
    public static GeoPoint of(final double longitude, final double latitude) {
        return new ArrayGeoPoint(longitude, latitude);
    }

    @Override
    public double getLongitude() {
        return coordinates[0];
    }

    @Override
    public double getLatitude() {
        return coordinates[1];
    }

    @NotNull
    public double[] getCoordinates() {
        return coordinates;
    }

    @NotNull
    @Override
    public String toString() {
        return "(lon=" + getLongitude() + ",lat=" + getLatitude() + ")";
    }
}
