package ru.hypernavi.commons;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import net.jcip.annotations.Immutable;

/**
 * Created by amosov-f on 29.12.15.
 */
@Immutable
public final class Image {
    @NotNull
    private final String link;
    @Nullable
    private final String thumbLink;
    @NotNull
    private final Dimension dimension;
    @NotNull
    private final Image[] duplicates;

    public Image(@NotNull final String link, @Nullable final String thumbLink, @NotNull final Dimension dimension, @NotNull final Image... duplicates) {
        this.link = link;
        this.thumbLink = thumbLink;
        this.dimension = dimension;
        this.duplicates = duplicates;
    }

    @NotNull
    public String getLink() {
        return link;
    }

    @Nullable
    public String getThumbLink() {
        return thumbLink;
    }

    @NotNull
    public String getThumbOrFull() {
        return thumbLink != null ? thumbLink : link;
    }

    @NotNull
    public Dimension getDimension() {
        return dimension;
    }

    @Nullable
    public Format getFormat() {
        for (final Format format : Format.values()) {
            if (link.toLowerCase().endsWith(format.getExtension())) {
                return format;
            }
        }
        for (final Format format : Format.values()) {
            if (link.toLowerCase().contains("." + format.getExtension())) {
                return format;
            }
        }
        return null;
    }

    @NotNull
    public Format getFormat(@NotNull final Format defaultFormat) {
        final Format format = getFormat();
        return format != null ? format : defaultFormat;
    }

    @NotNull
    public Image[] getDuplicates() {
        return duplicates;
    }

    public enum Format {
        JPG, PNG, GIF;

        @Nullable
        public static Format parse(@NotNull final String extension) {
            for (final Format format : values()) {
                if (format.getExtension().equals(extension)) {
                    return format;
                }
            }
            return null;
        }

        @NotNull
        public String getExtension() {
            return name().toLowerCase();
        }

        @NotNull
        public String getMimeType() {
            switch (this) {
                case JPG:
                    return "image/jpeg";
                case PNG:
                    return "image/png";
                case GIF:
                    return "image/gif";
                default:
                    throw new UnsupportedOperationException();
            }
        }
    }
}
