package ru.hypernavi.commons;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by amosov-f on 29.12.15.
 */
public final class Dimension {
    private final int width;
    private final int height;

    private Dimension(final int width, final int height) {
        this.width = width;
        this.height = height;
    }

    @NotNull
    public static Dimension of(final int width, final int height) {
        return new Dimension(width, height);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public boolean equals(@Nullable final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return width == ((Dimension) o).width && height == ((Dimension) o).height;

    }

    @Override
    public int hashCode() {
        return 31 * width + height;
    }
}
