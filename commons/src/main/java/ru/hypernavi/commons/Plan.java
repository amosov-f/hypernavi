package ru.hypernavi.commons;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import net.jcip.annotations.Immutable;

/**
 * Created by amosov-f on 07.11.15.
 */
@Immutable
public final class Plan extends Picture {
    public static final Plan[] EMPTY_ARRAY = new Plan[0];
    public static final Type TYPE = Type.PLAN;

    @Nullable
    private final Double azimuth;

    public Plan(@Nullable final String description, @NotNull final Image image, @Nullable final Double azimuth) {
        super(description, image);
        this.azimuth = azimuth;
    }

    @Nullable
    public Double getAzimuth() {
        return azimuth;
    }

    @NotNull
    @Override
    public Type getType() {
        return TYPE;
    }
}
