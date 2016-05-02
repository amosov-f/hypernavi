package ru.hypernavi.core.telegram.api.entity;

import org.jetbrains.annotations.NotNull;

/**
 * User: amosov-f
 * Date: 03.05.16
 * Time: 1:57
 */
public final class BotCommand extends Entity {
    private final int offset;
    private final int length;

    public BotCommand(final int offset, final int length) {
        super("bot_command");
        this.offset = offset;
        this.length = length;
    }

    public int getOffset() {
        return offset;
    }

    public int getLength() {
        return length;
    }

    @NotNull
    public String getCommand(@NotNull final String text) {
        return text.substring(offset, length);
    }
}
