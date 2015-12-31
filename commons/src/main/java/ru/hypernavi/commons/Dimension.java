package ru.hypernavi.commons;

import org.jetbrains.annotations.NotNull;

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
}
