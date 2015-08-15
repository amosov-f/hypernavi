package ru.hypernavi.core;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;


import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
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
            final String jsonFile = IOUtils.toString(getClass().getResourceAsStream(path));
            hypermarketInfo = new JSONObject(jsonFile);
            LOG.info("Hypermarket loaded from " + path);
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @NotNull
    public String getHash() {
        final String hash;
        try {
            hash = hypermarketInfo.getString("md5hash");
        } catch (JSONException e) {
            throw new RuntimeException(e.getMessage());
        }
        return hash;
    }
/*
    @NotNull
    public byte[] getSchema() {
        final byte[] result;
        try {
            final String pathScheme = "/okey/" + hypermarketInfo.getString("schemePath");
            result = IOUtils.toByteArray(getClass().getResourceAsStream(pathScheme));
        } catch (JSONException | IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return result;
    }
*/
    @NotNull
    public GeoPoint getCoordinate() {
        final double lat;
        final double lon;

        try {
            lat = hypermarketInfo.getDouble("latitude");
            lon = hypermarketInfo.getDouble("longitude");
        } catch (JSONException e) {
            throw new RuntimeException(e.getMessage());
        }
        return new GeoPoint(lat, lon);
    }

    @NotNull
    public String getAdress() {
        final String adress;
        try {
            adress = hypermarketInfo.getString("adress");
        } catch (JSONException e) {
            throw new RuntimeException(e.getMessage());
        }

        return adress;
    }

    @NotNull
    public String getType() {
        final String type;
        try {
            type = hypermarketInfo.getString("type");
        } catch (JSONException e) {
            throw new RuntimeException(e.getMessage());
        }
        return type;
    }
}
