package ru.hypernavi.commons;

import org.jetbrains.annotations.NotNull;

import ru.hypernavi.util.GeoPoint;

/**
 * Created by Константин on 28.07.2015.
 */
public class Hypermarket implements Positioned, Indexable {
    private final int id;
    @NotNull
    private final String adress;
    @NotNull
    private final String url;
    @NotNull
    private final String type;
    @NotNull
    private final GeoPoint location;

    public Hypermarket(final int id,
                       @NotNull final GeoPoint location,
                       @NotNull final String adress,
                       @NotNull final String type,
                       @NotNull final String url) {
        this.id = id;
        this.location = location;
        this.adress = adress;
        this.type = type;
        this.url = url;
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
    public String getUrl() {
        return url;
    }

    @NotNull
    public String getType() {
        return type;
    }

    @NotNull
    public String getAdress() {
        return adress;
    }
}
