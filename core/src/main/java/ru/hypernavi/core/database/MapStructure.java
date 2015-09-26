package ru.hypernavi.core.database;

import org.jetbrains.annotations.NotNull;

import java.util.List;


import ru.hypernavi.commons.Positioned;
import ru.hypernavi.util.GeoPoint;

/**
 * Created by Константин on 30.07.2015.
 */
public interface MapStructure<T extends Positioned> {
    @NotNull
    default T findClosest(final GeoPoint possition) {
        final List<T> closest = find(possition, 1);
        if (closest.size() == 0) {
            throw new IllegalStateException("No closest hyperfounded");
        }
        return closest.get(0);
    }

    List<T> getAll();

    @NotNull
    default List<T> findAll(final GeoPoint possition) {
        return find(possition, size());
    }

    @NotNull
    List<T> find(final GeoPoint possition, final int number);

    int size();

    void add(@NotNull final T hyper);
}
