package ru.hypernavi.ml.classifier;

import org.jetbrains.annotations.NotNull;

/**
 * Created by amosov-f on 03.09.15.
 */
public interface BinaryClassifier<T> extends Classifier<T> {
    boolean isClass(@NotNull final T object);

    @Override
    default int classify(@NotNull final T object) {
        return isClass(object) ? 1 : 0;
    }
}
