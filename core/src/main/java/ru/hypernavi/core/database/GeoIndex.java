package ru.hypernavi.core.database;

import org.jetbrains.annotations.NotNull;

import java.util.List;


import ru.hypernavi.commons.Positioned;
import ru.hypernavi.util.GeoPoint;

/**
 * Created by Константин on 30.07.2015.
 */
public interface GeoIndex<T extends Positioned> {
    @NotNull
    default T getNN(@NotNull final GeoPoint location) {
        return getKNN(location, 1).stream().findFirst().orElseThrow(() -> new IllegalStateException("No points in geo index!"));
    }

    @NotNull
    List<T> getKNN(@NotNull final GeoPoint location, final int k);

    @Deprecated
    List<T> getAll();

    @Deprecated
    void add(@NotNull final T hyper);
}
