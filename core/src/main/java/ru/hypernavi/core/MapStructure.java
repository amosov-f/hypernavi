package ru.hypernavi.core;

import org.jetbrains.annotations.NotNull;


import ru.hypernavi.util.GeoPoint;

/**
 * Created by Константин on 30.07.2015.
 */
public interface MapStructure<T extends Hypermarket> {
    @NotNull
    default Positioned findClosest(final GeoPoint possition) {
        return find(possition, 1)[0];
    }

    @NotNull
    default Positioned[] findAll(final GeoPoint possition) {
        return find(possition, size());
    }

    @NotNull
    Positioned[] find(final GeoPoint possition, final int number);


    Hypermarket get(final int id);

    int size();
}
