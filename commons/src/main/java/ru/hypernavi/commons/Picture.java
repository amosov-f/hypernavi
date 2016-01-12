package ru.hypernavi.commons;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by amosov-f on 12.01.16.
 */
public class Picture extends Hint {
    public static final Type TYPE = Type.PICTURE;

    @NotNull
    private final Image image;

    public Picture(@Nullable final String description, @NotNull final Image image) {
        super(description);
        this.image = image;
    }

    @NotNull
    public Image getImage() {
        return image;
    }

    @NotNull
    @Override
    public Type getType() {
        return TYPE;
    }
}
