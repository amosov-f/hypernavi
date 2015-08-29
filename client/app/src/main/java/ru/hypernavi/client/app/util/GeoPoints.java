package ru.hypernavi.client.app.util;

import org.jetbrains.annotations.NotNull;


import android.location.Location;
import ru.hypernavi.util.GeoPoint;

/**
 * Created by Acer on 22.08.2015.
 */
// TODO amosov-f: rename to GeoPointUtils
public enum GeoPoints {
    ;
    public static GeoPoint makeGeoPoint(@NotNull final Location location) {
        return new GeoPoint(location.getLongitude(), location.getLatitude());
    }
}
