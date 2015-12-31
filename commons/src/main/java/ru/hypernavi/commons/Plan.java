package ru.hypernavi.commons;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import net.jcip.annotations.Immutable;

/**
 * Created by amosov-f on 07.11.15.
 */
@Immutable
public final class Plan implements Hint {
    public static final Plan[] EMPTY_ARRAY = new Plan[0];

    @NotNull
    private final Image image;
    @Nullable
    private final Double azimuth;

    public Plan(@NotNull final Image image, @Nullable final Double azimuth) {
        this.image = image;
        this.azimuth = azimuth;
    }

    @NotNull
    public Image getImage() {
        return image;
    }

    @Nullable
    public Double getAzimuth() {
        return azimuth;
    }
}
