package ru.hypernavi.core;

import org.jetbrains.annotations.Nullable;


import ru.hypernavi.commons.Positioned;
import ru.hypernavi.util.GeoPoint;

/**
 * Created by Константин on 30.07.2015.
 */
public interface MapStructure<T extends Positioned> {
    @Nullable
    default T findClosest(final GeoPoint possition) {
        return find(possition, 1)[0];
    }

    @Nullable
    default T[] findAll(final GeoPoint possition) {
        return find(possition, size());
    }

    @Nullable
    T[] find(final GeoPoint possition, final int number);

    int size();
}
