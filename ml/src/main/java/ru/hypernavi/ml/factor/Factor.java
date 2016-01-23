package ru.hypernavi.ml.factor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.ToDoubleFunction;

/**
 * Created by amosov-f on 03.09.15.
 */
public abstract class Factor<T> implements ToDoubleFunction<T> {
    @NotNull
    private final String name;

    protected Factor(@NotNull final String name) {
        this.name = name;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return Objects.equals(name, ((Factor<?>) o).name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public static final class Lambda<T> extends Factor<T> {
        @NotNull
        private final ToDoubleFunction<T> delegate;

        public Lambda(@NotNull final String name, @NotNull final ToDoubleFunction<T> delegate) {
            super(name);
            this.delegate = delegate;
        }

        @Override
        public double applyAsDouble(@NotNull final T object) {
            return delegate.applyAsDouble(object);
        }
    }
}
