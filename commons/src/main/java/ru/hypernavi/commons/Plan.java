package ru.hypernavi.commons;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import net.jcip.annotations.Immutable;

/**
 * Created by amosov-f on 07.11.15.
 */
@Immutable
public final class Plan {
    public static final Plan[] EMPTY_ARRAY = new Plan[0];

    @NotNull
    private final String link;
    @Nullable
    private final Double azimuth;

    public Plan(@NotNull final String link, @Nullable final Double azimuth) {
        this.link = link;
        this.azimuth = azimuth;
    }

    @NotNull
    public String getLink() {
        return link;
    }

    @Nullable
    public Double getAzimuth() {
        return azimuth;
    }
}
