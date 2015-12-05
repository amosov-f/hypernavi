package ru.hypernavi.core.geoindex;

import org.jetbrains.annotations.NotNull;

import java.util.List;


import ru.hypernavi.commons.Positioned;
import ru.hypernavi.util.GeoPoint;

/**
 * Created by Константин on 30.07.2015.
 */
public interface GeoIndex<T extends Positioned> {
    @NotNull
    List<T> getNN(@NotNull final GeoPoint location, final int offset, final int count);

    @NotNull
    default T getNN(@NotNull final GeoPoint location) {
        return getKNN(location, 1).stream().findFirst().orElseThrow(() -> new IllegalStateException("No points in geo index!"));
    }

    @NotNull
    default List<T> getKNN(@NotNull final GeoPoint location, final int k) {
        return getNN(location, 0, k);
    }

    @Deprecated
    default List<T> getAll() {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default void add(@NotNull final T hyper) {
        throw new UnsupportedOperationException();
    }
}
