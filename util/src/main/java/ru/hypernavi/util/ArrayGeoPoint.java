package ru.hypernavi.util;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;


import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import ru.hypernavi.util.json.GsonUtils;

/**
 * Created by amosov-f on 20.12.15.
 */
public final class ArrayGeoPoint implements GeoPoint {
    static {
        GsonUtils.registerTypeAdapter(GeoPoint.class, new JsonDeserializer<GeoPoint>() {
            @Override
            public GeoPoint deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
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
}
