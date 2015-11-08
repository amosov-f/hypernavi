package ru.hypernavi.core.session;

import org.jetbrains.annotations.NotNull;

/**
 * Created by amosov-f on 08.11.15.
 */
public final class SessionInitializationException extends Exception {
    public SessionInitializationException(@NotNull final String detailMessage) {
        super(detailMessage);
    }
}
