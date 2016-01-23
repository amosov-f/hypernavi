package ru.hypernavi.ml.regression;

import org.jetbrains.annotations.NotNull;

import java.util.function.ToDoubleFunction;

/**
 * Created by amosov-f on 23.01.16.
 */
public interface Regression<T> extends ToDoubleFunction<T> {
    double predict(@NotNull final T object);

    @Override
    default double applyAsDouble(@NotNull final T object) {
        return predict(object);
    }
}
