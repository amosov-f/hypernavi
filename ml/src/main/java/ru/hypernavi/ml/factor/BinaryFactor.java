package ru.hypernavi.ml.factor;

import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

/**
 * Created by amosov-f on 03.09.15.
 */
public abstract class BinaryFactor<T> extends ClassFactor<T> implements Predicate<T> {
    protected BinaryFactor(@NotNull final String name) {
        super(name);
    }

    @Override
    public int applyAsInt(@NotNull final T object) {
        return test(object) ? 1 : 0;
    }
}
