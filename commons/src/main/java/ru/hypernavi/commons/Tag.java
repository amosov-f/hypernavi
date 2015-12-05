package ru.hypernavi.commons;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Created by amosov-f on 28.11.15.
 */
public final class Tag implements Comparable<Tag> {
    @NotNull
    private final String name;

    public Tag(@NotNull final String name) {
        this.name = name;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return Objects.equals(name, ((Tag) o).name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public int compareTo(@NotNull final Tag tag) {
        return name.compareTo(tag.getName());
    }
}
