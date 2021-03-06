package ru.hypernavi.core.session;

import com.google.inject.ImplementedBy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Created by amosov-f on 21.11.15.
 */
@SuppressWarnings("ClassReferencesSubclass")
@ImplementedBy(SessionImpl.class)
public interface Session {
    String REQ_ID = "reqid";

    @NotNull
    String getId();

    long getTimestamp();

    <T> void set(@NotNull final Property<? super T> property, @NotNull final T value);

    default <T> void setIfNotNull(@NotNull final Property<? super T> property, @Nullable final T value) {
        if (value != null) {
            set(property, value);
        }
    }

    @Nullable
    <T> T get(@NotNull final Property<T> property);

    @NotNull
    default <T> T get(@NotNull final Property<T> property, @NotNull final T defaultValue) {
        return getOptional(property).orElse(defaultValue);
    }

    @NotNull
    default <T> Optional<T> getOptional(@NotNull final Property<T> property) {
        return Optional.ofNullable(get(property));
    }

    @NotNull
    default <T> T demand(@NotNull final Property<T> property) {
        return getOptional(property).orElseThrow(() -> new IllegalStateException("There is no '" + property + "' property in session!"));
    }

    default boolean has(@NotNull final Property<?> property) {
        return get(property) != null;
    }

    default <T> void ifHas(@NotNull final Property<T> property, @NotNull final Consumer<? super T> consumer) {
        if (has(property)) {
            consumer.accept(demand(property));
        }
    }
}
