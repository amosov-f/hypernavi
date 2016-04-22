package ru.hypernavi.core.telegram.api.markup;

import org.jetbrains.annotations.NotNull;

/**
 * User: amosov-f
 * Date: 23.04.16
 * Time: 1:17
 */
public final class KeyboardButton {
    @NotNull
    private final String text;
    private final boolean requestContact;
    private final boolean requestLocation;

    public KeyboardButton(@NotNull final String text, final boolean requestContact, final boolean requestLocation) {
        this.text = text;
        this.requestContact = requestContact;
        this.requestLocation = requestLocation;
    }
}
