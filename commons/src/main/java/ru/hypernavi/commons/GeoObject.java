package ru.hypernavi.commons;

import org.jetbrains.annotations.NotNull;


import net.jcip.annotations.Immutable;
import ru.hypernavi.util.GeoPoint;

/**
 * Created by amosov-f on 07.11.15.
 */
@Immutable
public final class GeoObject implements Positioned {
    @NotNull
    private final String name;
    @NotNull
    private final String description;
    @NotNull
    private final GeoPoint location;

    public GeoObject(@NotNull final String name, @NotNull final String description, @NotNull final GeoPoint location) {
        this.name = name;
        this.description = description;
        this.location = location;
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
    @Override
    public GeoPoint getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "GeoObject{" + "name='" + name + '\'' + ", description='" + description + '\'' + ", location=" + location + '}';
    }
}
