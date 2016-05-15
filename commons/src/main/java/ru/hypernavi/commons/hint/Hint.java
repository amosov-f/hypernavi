package ru.hypernavi.commons.hint;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by amosov-f on 29.12.15.
 */
public abstract class Hint {
    @Nullable
    private final String description;
    @Nullable
    private Integer authorUid;

    protected Hint(@Nullable final String description) {
        this.description = description;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    @NotNull
    public abstract Type getType();

    @Nullable
    public Integer getAuthorUid() {
        return authorUid;
    }

    public void setAuthorUid(@Nullable final Integer authorUid) {
        this.authorUid = authorUid;
    }

    public enum Type {
        PLAN, PICTURE, TEXT
    }
}
