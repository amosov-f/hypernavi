package ru.hypernavi.core.session;

import org.jetbrains.annotations.NotNull;

/**
 * Created by amosov-f on 24.10.15.
 */
public interface SessionInitializer {
    void initialize(@NotNull Session session);

    void validate(@NotNull final Session session) throws SessionValidationException;
}
