package ru.hypernavi.commons;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.List;


import ru.hypernavi.util.GeoPoint;

/**
 * Created by Константин on 15.08.2015.
 */
public class InfoResponce {
    private final List<Hypermarket> closestMarkets;
    private final GeoPoint requestLocation;

    public InfoResponce(@Nullable final List<Hypermarket> closestMarkets, @NotNull final GeoPoint requestLocation) {
        this.closestMarkets = closestMarkets;
        this.requestLocation = requestLocation;
    }

    @NotNull
    public GeoPoint getLocation() {
        return requestLocation;
    }

    @Nullable
    public List<Hypermarket> getClosestMarkets() {
        return closestMarkets;
    }
}
