package ru.hypernavi.core;

import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;


import ru.hypernavi.util.GeoPoint;

/**
 * Created by Константин on 28.07.2015.
 */
public class Hypermarket implements GeoPointed {
    private final GeoPoint coordinate;
    private final BufferedImage schema;

    public Hypermarket(final GeoPoint coordinate, final BufferedImage schema) {
        this.coordinate = coordinate;
        this.schema = schema;
    }

    @NotNull
    public BufferedImage getSchema() {
        return schema;
    }

    @NotNull
    @Override
    public GeoPoint getLocation() {
        return coordinate;
    }

    @NotNull
    @Override
    public int getId() {
        return 0;
    }
}
