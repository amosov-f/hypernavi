package ru.hypernavi.commons;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import net.jcip.annotations.Immutable;

/**
 * Created by amosov-f on 29.12.15.
 */
@Immutable
public final class Image implements Hint {
    @NotNull
    private final String link;
    private final Dimension dimension;
    @NotNull
    private final Image[] duplicates;

    public Image(@NotNull final String link, @NotNull final Dimension dimension, @NotNull final Image... duplicates) {
        this.link = link;
        this.dimension = dimension;
        this.duplicates = duplicates;
    }

    @NotNull
    public String getLink() {
        return link;
    }

    @NotNull
    public Dimension getDimension() {
        return dimension;
    }

    @Nullable
    public Format getFormat() {
        for (final Format format : Format.values()) {
            if (link.toLowerCase().endsWith(format.name().toLowerCase())) {
                return format;
            }
        }
        return null;
    }

    @NotNull
    public Image[] getDuplicates() {
        return duplicates;
    }

    public enum Format {
        JPG, PNG, GIF
    }
}
