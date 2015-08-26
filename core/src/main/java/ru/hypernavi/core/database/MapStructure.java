package ru.hypernavi.core.database;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import java.util.List;


import ru.hypernavi.commons.Hypermarket;
import ru.hypernavi.commons.Positioned;
import ru.hypernavi.util.GeoPoint;

/**
 * Created by Константин on 30.07.2015.
 */
public interface MapStructure<T extends Positioned> {
    @Nullable
    default T findClosest(final GeoPoint possition) {
        final List<T> closest = find(possition, 1);
        return closest == null ? null : closest.get(0);
    }

    @Nullable
    default List<T> findAll(final GeoPoint possition) {
        return find(possition, size());
    }

    @Nullable
    List<T> find(final GeoPoint possition, final int number);

    int size();

    void add(@NotNull final T hyper);
}
