package ru.hypernavi.core.telegram;

import org.jetbrains.annotations.Nullable;

/**
 * Created by amosov-f on 18.10.15.
 */
public final class Update {
    private final int updateId;
    @Nullable
    private final Message message;

    public Update(final int updateId, @Nullable final Message message) {
        this.updateId = updateId;
        this.message = message;
    }

    public int getUpdateId() {
        return updateId;
    }

    @Nullable
    public Message getMessage() {
        return message;
    }
}
