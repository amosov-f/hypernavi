package ru.hypernavi.core.telegram.api.inline;

import org.jetbrains.annotations.NotNull;

/**
 * User: amosov-f
 * Date: 22.04.16
 * Time: 23:31
 */
public abstract class InlineQueryResult {
    @NotNull
    private final String type;

    protected InlineQueryResult(@NotNull final String type) {
        this.type = type;
    }
}
