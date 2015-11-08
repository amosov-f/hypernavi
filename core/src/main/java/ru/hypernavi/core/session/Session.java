package ru.hypernavi.core.session;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by amosov-f on 24.10.15.
 */
public final class Session {
    @NotNull
    private final Map<Property<?>, Object> properties = new HashMap<>();

    public <T> void set(@NotNull final Property<? super T> property, @NotNull final T value) {
        final Object prevValue = properties.put(property, value);
        if (prevValue != null) {
            throw new IllegalStateException("Property already exists: " + property + "=" + prevValue + "!");
        }
    }

    public <T> void setIfNotNull(@NotNull final Property<? super T> property, @Nullable final T value) {
        if (value != null) {
            set(property, value);
        }
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T get(@NotNull final Property<T> property) {
        return (T) properties.get(property);
    }

    @NotNull
    public <T> T demand(@NotNull final Property<T> property) {
        return Objects.requireNonNull(get(property), "There is no '" + property + "' property in session!");
    }

    public boolean has(@NotNull final Property<?> property) {
        return properties.containsKey(property);
    }
}
