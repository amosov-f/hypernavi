package ru.hypernavi.core.telegram.api.entity;

import org.jetbrains.annotations.NotNull;

/**
 * User: amosov-f
 * Date: 03.05.16
 * Time: 1:56
 */
public abstract class Entity {
    public static final Entity[] EMPTY_ARRAY = new Entity[0];

    @NotNull
    private final String type;

    protected Entity(@NotNull final String type) {
        this.type = type;
    }
}
