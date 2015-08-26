package ru.hypernavi.core;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import ru.hypernavi.commons.Hypermarket;
import ru.hypernavi.util.GeoPoint;

/**
 * Created by Константин on 14.08.2015.
 */
public class HypermarketReader {
    private static final Log LOG = LogFactory.getLog(HypermarketReader.class);

    @NotNull
    private final JSONObject hypermarketInfo;

    public HypermarketReader(final String path) {
        LOG.info("Reading hypermarket from " + path);
        try {
            final StringWriter writer = new StringWriter();
            final String jsonFile = IOUtils.toString(getClass().getResourceAsStream(path));

            //final InputStream in = new FileInputStream(new File(path));
            //IOUtils.copy(in, writer, "UTF-8");
            //final String jsonFile = writer.toString();
            hypermarketInfo = new JSONObject(jsonFile);
            LOG.info("Hypermarket loaded from " + path);
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public int getId() {
        final int id;
        try {
            id = hypermarketInfo.getInt("id");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return id;
    }

    @NotNull
    public Hypermarket construct() {
        return new Hypermarket(getId(), getCoordinate(), getAddress(), getType(), getUrl());
    }

    @NotNull
    public String getUrl() {
        final String url;
        try {
            url = hypermarketInfo.getString("url");
        } catch (JSONException ignored) {
            return "No schema founded";
        }
        return url;
    }

    @NotNull
    public GeoPoint getCoordinate() {
        final double lat;
        final double lon;

        try {
            lat = hypermarketInfo.getDouble("latitude");
            lon = hypermarketInfo.getDouble("longitude");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return new GeoPoint(lon, lat);
    }

    @NotNull
    public String getAddress() {
        final String address;
        try {
            address = hypermarketInfo.getString("address");
        } catch (JSONException ignored) {
            return "No address found";
        }

        return address;
    }

    @NotNull
    public String getType() {
        final String type;
        try {
            type = hypermarketInfo.getString("type");
        } catch (JSONException ignored) {
            return "No type founded";
        }
        return type;
    }
}
