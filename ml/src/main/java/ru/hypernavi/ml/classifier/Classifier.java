package ru.hypernavi.ml.classifier;

import org.jetbrains.annotations.NotNull;

/**
 * Created by amosov-f on 03.09.15.
 */
public interface Classifier<T> {
    int classify(@NotNull final T object);
}
