package ru.hypernavi.core.session;

import org.jetbrains.annotations.NotNull;

/**
 * Created by amosov-f on 08.11.15.
 */
public final class SessionValidationException extends Exception {
    @NotNull
    private final Error error;

    public SessionValidationException(@NotNull final String detailMessage) {
        this(Error.BAD_REQUEST, detailMessage);
    }

    public SessionValidationException(@NotNull final Error error, @NotNull final String detailMessage) {
        super(detailMessage);
        this.error = error;
    }

    @NotNull
    public Error getError() {
        return error;
    }

    public enum Error {
        BAD_REQUEST, UNAUTHORIZED, FORBIDDEN
    }
}
