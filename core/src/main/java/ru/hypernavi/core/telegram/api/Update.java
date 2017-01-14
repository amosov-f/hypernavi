package ru.hypernavi.core.telegram.api;

import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import ru.hypernavi.core.telegram.api.inline.InlineQuery;

import java.util.Objects;

/**
 * Created by amosov-f on 18.10.15.
 */
public final class Update {
    private final int updateId;
    @Nullable
    private final Message message;
    @Nullable
    private final InlineQuery inlineQuery;

    @Nullable
    private String reqId;
    private long receiptTimestamp;
    @Nullable
    private JsonElement rawUpdate;

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

    @NotNull
    public String getReqId() {
        return Objects.requireNonNull(reqId, "No reqId in update!");
    }

    public void setReqId(@NotNull final String reqId) {
        this.reqId = reqId;
    }

    public void setReceiptTimestamp(final long receiptTimestamp) {
        this.receiptTimestamp = receiptTimestamp;
    }

    public long getReceiptTimestamp() {
        return receiptTimestamp;
    }

    @Nullable
    public JsonElement getRawUpdate() {
        return rawUpdate;
    }

    public void setRawUpdate(@Nullable final JsonElement rawUpdate) {
        this.rawUpdate = rawUpdate;
    }

    @NotNull
    @Override
    public String toString() {
        return TelegramApi.gson().toJson(this);
    }
}
