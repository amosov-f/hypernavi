package ru.hypernavi.core.telegram.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import ru.hypernavi.core.telegram.api.entity.BotCommand;
import ru.hypernavi.core.telegram.api.entity.Entity;
import ru.hypernavi.util.GeoPoint;

/**
 * Created by amosov-f on 18.10.15.
 */
public final class Message {
    private final int messageId;
    @NotNull
    private final Chat chat;
    @Nullable
    private final String text;
    @Nullable
    private final GeoPoint location;
    @Nullable
    private final BotCommand[] entities;

    public Message(final int messageId,
                   @NotNull final Chat chat,
                   @Nullable final String text,
                   @Nullable final GeoPoint location,
                   @Nullable final BotCommand... entities)
    {
        this.messageId = messageId;
        this.chat = chat;
        this.text = text;
        this.location = location;
        this.entities = entities;
    }

    public int getMessageId() {
        return messageId;
    }

    @NotNull
    public Chat getChat() {
        return chat;
    }

    @Nullable
    public String getText() {
        return text;
    }

    @Nullable
    public GeoPoint getLocation() {
        return location;
    }

    @NotNull
    public Entity[] getEntities() {
        return entities != null ? entities : Entity.EMPTY_ARRAY;
    }
}
