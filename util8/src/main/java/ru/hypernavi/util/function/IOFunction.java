package ru.hypernavi.util.function;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * Created by amosov-f on 30.12.15.
 */
@FunctionalInterface
public interface IOFunction<T, R> {
    @Nullable
    R apply(@NotNull T t) throws IOException;

    @NotNull
    static <T> IOFunction<T, T> identity() {
        return t -> t;
    }
}
