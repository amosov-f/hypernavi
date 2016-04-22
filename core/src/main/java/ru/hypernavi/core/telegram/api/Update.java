package ru.hypernavi.core.telegram.api;

import org.jetbrains.annotations.Nullable;


import ru.hypernavi.core.telegram.api.inline.InlineQuery;

/**
 * Created by amosov-f on 18.10.15.
 */
public final class Update {
    private final int updateId;
    @Nullable
    private final Message message;
    @Nullable
    private final InlineQuery inlineQuery;

    public Update(final int updateId, @Nullable final Message message, @Nullable final InlineQuery inlineQuery) {
        this.updateId = updateId;
        this.message = message;
        this.inlineQuery = inlineQuery;
    }

    public int getUpdateId() {
        return updateId;
    }

    @Nullable
    public Message getMessage() {
        return message;
    }

    @Nullable
    public InlineQuery getInlineQuery() {
        return inlineQuery;
    }
}
