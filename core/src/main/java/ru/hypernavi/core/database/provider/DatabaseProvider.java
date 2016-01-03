package ru.hypernavi.core.database.provider;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Created by amosov-f on 23.11.15.
 */
public interface DatabaseProvider<T> {
    @Nullable
    T get(@NotNull String id);

    @NotNull
    default Stream<T> get(@NotNull final String[] ids) {
        return Arrays.stream(ids).map(this::get);
    }

    default boolean has(@NotNull final String id) {
        return get(id) != null;
    }

    @NotNull
    String add(@NotNull T obj);

    @NotNull
    default String put(@NotNull final T obj) {
        return add(obj);
    }

    @Nullable
    T remove(String id);

    default void put(@NotNull final String id, @NotNull final T obj) {
        throw new UnsupportedOperationException();
    }
}
