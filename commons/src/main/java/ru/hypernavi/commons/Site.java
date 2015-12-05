package ru.hypernavi.commons;

import org.jetbrains.annotations.NotNull;


import net.jcip.annotations.Immutable;
import ru.hypernavi.util.GeoPoint;

/**
 * Created by amosov-f on 07.11.15.
 */
@Immutable
public class Site implements Positioned {
    @NotNull
    private final GeoObject position;
    @NotNull
    private final Plan[] plans;

    public Site(@NotNull final GeoObject position, @NotNull final Plan... plans) {
        this.position = position;
        this.plans = plans;
    }

    @NotNull
    public final GeoObject getPosition() {
        return position;
    }

    @NotNull
    public final Plan[] getPlans() {
        return plans;
    }

    @NotNull
    @Override
    public GeoPoint getLocation() {
        return position.getLocation();
    }
}
