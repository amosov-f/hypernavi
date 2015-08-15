package ru.hypernavi.core;

import org.jetbrains.annotations.NotNull;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ru.hypernavi.util.GeoPoint;

/**
 * Created by Константин on 28.07.2015.
 */
// TODO: take some logic to another classes (reading, add interfaces)
public class Hypermarket implements Positioned, Indexable {
    private static final Log LOG = LogFactory.getLog(Hypermarket.class);
    private static int maxId = 0;
    private final int id;
    @NotNull
    private final String adress;
    @NotNull
    private final String md5hash;
    @NotNull
    private final String type;
    @NotNull
    private final GeoPoint coordinate;

    public Hypermarket(final String path) {
        id = maxId;
        maxId++;
        final HypermarketReader reader = new HypermarketReader(path);
        coordinate = reader.getCoordinate();
        adress = reader.getAdress();
        md5hash = reader.getHash();
        type = reader.getType();
    }


    @NotNull
    @Override
    public GeoPoint getLocation() {
        return coordinate;
    }

    @Override
    public int getId() {
        return id;
    }

    @NotNull
    public String getMd5hash() {
        return md5hash;
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
