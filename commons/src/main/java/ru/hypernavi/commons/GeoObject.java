package ru.hypernavi.commons;

import org.jetbrains.annotations.NotNull;


import ru.hypernavi.util.GeoPoint;

/**
 * Created by amosov-f on 07.11.15.
 */
public final class GeoObject {
    @NotNull
    private final String name;
    @NotNull
    private final String description;
    @NotNull
    private final GeoPoint position;

    public GeoObject(@NotNull final String name, @NotNull final String description, @NotNull final GeoPoint position) {
        this.name = name;
        this.description = description;
        this.position = position;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public String getDescription() {
        return description;
    }

    @NotNull
    public GeoPoint getPosition() {
        return position;
    }
}
