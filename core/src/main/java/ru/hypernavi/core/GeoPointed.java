package ru.hypernavi.core;

import org.jetbrains.annotations.NotNull;


import ru.hypernavi.util.GeoPoint;

/**
 * Created by Константин on 30.07.2015.
 */
public interface GeoPointed {
    @NotNull
    GeoPoint getLocation();

    @NotNull
    int getId();
}
