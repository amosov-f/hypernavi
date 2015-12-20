package ru.hypernavi.client.app.util;

import org.jetbrains.annotations.NotNull;


import android.location.Location;
import ru.hypernavi.util.GeoPointImpl;

/**
 * Created by Acer on 22.08.2015.
 */
public enum GeoPointsUtils {
    ;

    public static GeoPointImpl makeGeoPoint(@NotNull final Location location) {
        return new GeoPointImpl(location.getLongitude(), location.getLatitude());
    }
}
