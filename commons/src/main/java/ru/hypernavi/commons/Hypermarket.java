package ru.hypernavi.commons;

import org.jetbrains.annotations.NotNull;


import ru.hypernavi.util.GeoPoint;

/**
 * Created by Константин on 28.07.2015.
 */
public class Hypermarket implements Positioned, Indexable {
    private final int id;
    @NotNull
    private final String address;
    @NotNull
    private final String path;
    @NotNull
    private final String type;
    @NotNull
    private final GeoPoint location;
    @NotNull
    private final String url;

    private final double angle;


    public Hypermarket(final int id,
                       @NotNull final GeoPoint location,
                       @NotNull final String address,
                       @NotNull final String type,
                       @NotNull final String path,
                       @NotNull final String url,
                       final double angle) {
        this.id = id;
        this.location = location;
        this.address = address;
        this.type = type;
        this.path = path;
        this.url = url;
        this.angle = angle;
    }

    @NotNull
    public String getUrl() {
        return url;
    }

    double getAngle() {
        return angle;
    }

    @NotNull
    @Override
    public GeoPoint getLocation() {
        return location;
    }

    @Override
    public int getId() {
        return id;
    }

    @NotNull
    public String getPath() {
        return path;
    }

    @NotNull
    public String getType() {
        return type;
    }

    @NotNull
    public String getAddress() {
        return address;
    }
}
