package ru.hypernavi.ml.classifier;

import org.jetbrains.annotations.NotNull;

import java.util.function.ToIntFunction;

/**
 * Created by amosov-f on 03.09.15.
 */
public interface Classifier<T> extends ToIntFunction<T> {
    int classify(@NotNull final T object);

    @Override
    default int applyAsInt(@NotNull final T object) {
        return classify(object);
    }
}
