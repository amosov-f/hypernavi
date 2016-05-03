package ru.hypernavi.core.session;

import org.jetbrains.annotations.NotNull;

/**
 * Created by amosov-f on 24.10.15.
 */
public interface SessionInitializer {
    void initialize(@NotNull Session session);

    void validate(@NotNull final Session session) throws SessionValidationException;

    default void validate(@NotNull final Session session, @NotNull final Property<?>... properties) throws SessionValidationException {
        for (final Property<?> property : properties) {
            if (!session.has(property)) {
                throw new SessionValidationException("No '" + property + "' in request!");
            }
        }
    }
}
