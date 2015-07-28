package ru.hypernavi.core;

import java.awt.image.BufferedImage;


import ru.hypernavi.util.GeoPoint;

/**
 * Created by Константин on 28.07.2015.
 */
public class Hypernavi {
    private final GeoPoint coordinate;
    private final BufferedImage schema;
    public Hypernavi(final GeoPoint coordinate, final BufferedImage schema) {
        this.coordinate = coordinate;
        this.schema = schema;
    }
    public GeoPoint getCoordinate() {
        return coordinate;
    }
    public BufferedImage getSchema() {
        return schema;
    }
}
