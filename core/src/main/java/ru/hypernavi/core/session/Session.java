package ru.hypernavi.core.session;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

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

    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T get(@NotNull final Property<T> property) {
        return (T) properties.get(property);
    }
}
