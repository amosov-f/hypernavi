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
    private final int date;
    @Nullable
    private final GeoPoint location;
    @Nullable
    private final BotCommand[] entities;
    @Nullable
    private final PhotoSize[] photo;

    public Message(final int messageId,
                   @NotNull final Chat chat,
                   @Nullable final String text,
                   final int date,
                   @Nullable final GeoPoint location,
                   @Nullable final BotCommand[] entities,
                   @Nullable final PhotoSize[] photo)
    {
        this.messageId = messageId;
        this.chat = chat;
        this.text = text;
        this.date = date;
        this.location = location;
        this.entities = entities;
        this.photo = photo;
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

    public int getDate() {
        return date;
    }

    @Nullable
    public GeoPoint getLocation() {
        return location;
    }

    @NotNull
    public Entity[] getEntities() {
        return entities != null ? entities : Entity.EMPTY_ARRAY;
    }

    @NotNull
    public PhotoSize[] getPhoto() {
        return photo != null ? photo : PhotoSize.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public String toString() {
        return TelegramApi.gson().toJson(this);
    }
}
