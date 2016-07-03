package ru.hypernavi.core.session;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by amosov-f on 24.10.15.
 */
public class SessionImpl implements Session {
    @NotNull
    private final String id;
    private final long timestamp = System.currentTimeMillis();

    @NotNull
    private final Map<Property<?>, Object> properties = new HashMap<>();

    public SessionImpl() {
        // noinspection MagicNumber
        this.id = timestamp + "-" + ThreadLocalRandom.current().nextInt(1000, 10000);
    }

    @NotNull
    @Override
    public String getId() {
        return id;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public <T> void set(@NotNull final Property<? super T> property, @NotNull final T value) {
        final Object prevValue = properties.put(property, value);
        if (prevValue != null) {
            throw new IllegalStateException("Property already exists: " + property + "=" + prevValue + "!");
        }
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(@NotNull final Property<T> property) {
        return (T) properties.get(property);
    }

    @Override
    public boolean has(@NotNull final Property<?> property) {
        return properties.containsKey(property);
    }
}
