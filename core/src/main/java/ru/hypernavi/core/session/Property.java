package ru.hypernavi.core.session;

import org.jetbrains.annotations.NotNull;

/**
 * Created by amosov-f on 24.10.15.
 */
public final class Property<T> {
    public static final Property<String> TEXT = new Property<>("text");

    @NotNull
    private final String name;

    public Property(@NotNull final String name) {
        this.name = name;
    }

    @NotNull
    @Override
    public String toString() {
        return name;
    }
}
