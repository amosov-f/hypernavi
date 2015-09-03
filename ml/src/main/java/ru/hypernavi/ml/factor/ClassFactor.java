package ru.hypernavi.ml.factor;

import org.jetbrains.annotations.NotNull;

import java.util.function.ToIntFunction;

/**
 * Created by amosov-f on 04.09.15.
 */
public abstract class ClassFactor<T> extends Factor<T> implements ToIntFunction<T> {
    protected ClassFactor(@NotNull final String name) {
        super(name);
    }

    @Override
    public double applyAsDouble(@NotNull final T object) {
        return applyAsInt(object);
    }
}
