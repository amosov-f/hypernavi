package ru.hypernavi.core;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Константин on 30.07.2015.
 */
public interface MapStructure {
    @NotNull
    GeoPointed findClosest(GeoPointed possition);
    @NotNull
    GeoPointed[] findAll(GeoPointed possition);
    @NotNull
    GeoPointed[] find(GeoPointed possition, int number);
    int size();
}
