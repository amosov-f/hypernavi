package ru.hypernavi.core.geoindex;

import org.jetbrains.annotations.NotNull;

import java.util.List;


import ru.hypernavi.commons.Index;
import ru.hypernavi.commons.Positioned;
import ru.hypernavi.util.GeoPoint;
import ru.hypernavi.util.GeoPointImpl;

/**
 * Created by Константин on 30.07.2015.
 */
public interface GeoIndex<T extends Positioned> {
    @NotNull
    List<Index<? extends T>> getNN(@NotNull final GeoPoint location, final int offset, final int count);

    @NotNull
    default Index<? extends T> getNN(@NotNull final GeoPointImpl location) {
        return getKNN(location, 1).stream().findFirst().orElseThrow(() -> new IllegalStateException("No points in geo index!"));
    }

    @NotNull
    default List<Index<? extends T>> getKNN(@NotNull final GeoPointImpl location, final int k) {
        return getNN(location, 0, k);
    }

    @Deprecated
    default List<T> getAll() {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    default String add(@NotNull final T hyper) {
        throw new UnsupportedOperationException();
    }
}
