package ru.hypernavi.commons;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import net.jcip.annotations.Immutable;

/**
 * Created by amosov-f on 29.12.15.
 */
@Immutable
public final class Image extends Hint {
    public static final Type TYPE = Type.IMAGE;

    @NotNull
    private final String link;
    private final Dimension dimension;
    @NotNull
    private final Image[] duplicates;

    public Image(@NotNull final String link,
                 @NotNull final Dimension dimension,
                 @NotNull final Image... duplicates)
    {
        this(null, link, dimension, duplicates);
    }

    public Image(@Nullable final String description,
                 @NotNull final String link,
                 @NotNull final Dimension dimension,
                 @NotNull final Image... duplicates)
    {
        super(description);
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
            if (link.toLowerCase().endsWith(format.getExtension())) {
                return format;
            }
        }
        return null;
    }

    @NotNull
    public Image[] getDuplicates() {
        return duplicates;
    }

    @NotNull
    @Override
    public Type getType() {
        return TYPE;
    }

    public enum Format {
        JPG, PNG, GIF;

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
