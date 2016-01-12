package ru.hypernavi.commons;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by amosov-f on 29.12.15.
 */
public abstract class Hint {
    @Nullable
    private final String description;

    protected Hint(@Nullable final String description) {
        this.description = description;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    @NotNull
    public abstract Type getType();

    public enum Type {
        PLAN, PICTURE
    }
}
