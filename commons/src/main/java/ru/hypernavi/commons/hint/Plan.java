package ru.hypernavi.commons.hint;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import net.jcip.annotations.Immutable;
import ru.hypernavi.commons.Image;
import ru.hypernavi.commons.PointMap;

/**
 * Created by amosov-f on 07.11.15.
 */
@Immutable
public final class Plan extends Picture {
    public static final Plan[] EMPTY_ARRAY = new Plan[0];
    public static final Type TYPE = Type.PLAN;

    @Nullable
    private final Double azimuth;
    @NotNull
    private final PointMap[] points;

    public Plan(@Nullable final String description, @NotNull final Image image, @Nullable final Double azimuth, @NotNull final PointMap... points) {
        super(description, image);
        this.azimuth = azimuth;
        this.points = points;
    }

    @Nullable
    public Double getAzimuth() {
        return azimuth;
    }

    @NotNull
    public PointMap[] getPoints() {
        return points;
    }

    @NotNull
    @Override
    public Type getType() {
        return TYPE;
    }
}
