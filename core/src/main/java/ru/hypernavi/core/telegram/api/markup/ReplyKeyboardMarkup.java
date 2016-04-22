package ru.hypernavi.core.telegram.api.markup;

import org.jetbrains.annotations.NotNull;

/**
 * User: amosov-f
 * Date: 23.04.16
 * Time: 1:16
 */
public final class ReplyKeyboardMarkup implements ReplyMarkup {
    @NotNull
    private final KeyboardButton[][] keyboard;

    public ReplyKeyboardMarkup(@NotNull final KeyboardButton[]... keyboard) {
        this.keyboard = keyboard;
    }
}
