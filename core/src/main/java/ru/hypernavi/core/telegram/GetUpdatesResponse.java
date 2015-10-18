package ru.hypernavi.core.telegram;

import org.jetbrains.annotations.NotNull;

/**
 * Created by amosov-f on 18.10.15.
 */
public final class GetUpdatesResponse {
    private final boolean ok;
    @NotNull
    private final Update[] result;

    public GetUpdatesResponse(final boolean ok, @NotNull final Update... result) {
        this.ok = ok;
        this.result = result;
    }

    public boolean isOk() {
        return ok;
    }

    @NotNull
    public Update[] getResult() {
        return result;
    }
}
