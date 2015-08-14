package ru.hypernavi.core;

import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ru.hypernavi.util.GeoPoint;

/**
 * Created by Константин on 28.07.2015.
 */

public class Hypermarket implements Positioned {
    private static final Log LOG = LogFactory.getLog(Hypermarket.class);
    private static int maxId = 0;
    private final int id;
    private final String adress;
    private final String md5hash;
    private final String type;
    private final BufferedImage schema;
    private final GeoPoint coordinate;

    public Hypermarket(final String path) {
        id = maxId++;
        final HypermarketReader reader = new HypermarketReader(path);
        coordinate = reader.getCoordinate();
        schema = reader.getSchema();
        adress = reader.getAdress();
        md5hash = ImageHash.generate(schema);
        type = reader.getType();
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

    public int getID() {
        return id;
    }

    public String getMd5hash() {
        return  md5hash;
    }
}
