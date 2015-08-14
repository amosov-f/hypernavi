package ru.hypernavi.core;

import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;


import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import ru.hypernavi.util.GeoPoint;

/**
 * Created by Константин on 14.08.2015.
 */
public class HypermarketReader {
    private JSONObject hypermarketInfo;
    public HypermarketReader(final String path) {
        try {
            final String jsonFile = IOUtils.toString(getClass().getResourceAsStream(path));
            hypermarketInfo = new JSONObject(jsonFile);

        } catch (IOException e) {

        } catch (JSONException e) {

        }
    }

    public BufferedImage getSchema() {
        BufferedImage result = null;
        try {
            final String pathScheme = hypermarketInfo.getString("schemePath");
            result = ImageIO.read(getClass().getResourceAsStream("/" + pathScheme));
        } catch (JSONException e) {
            //e.printStackTrace();
        } catch (IOException e) {
            //e.printStackTrace();
        }
        return result;
    }

    @NotNull
    public GeoPoint getCoordinate() {
        double lat = 0.0;
        double lon = 0.0;

        try {
            lat = hypermarketInfo.getDouble("latitude");
            lon = hypermarketInfo.getDouble("longitude");
        } catch (JSONException e) {
            //  e.printStackTrace();
        }
        return new GeoPoint(lat, lon);
    }

    @NotNull
    public String getAdress() {
        String adress = "default";
        try {
            adress = hypermarketInfo.getString("adress");
        } catch (JSONException e) {
            //
        }

        return adress;
    }

    @NotNull
    public String getType() {
        String type = "default";
        try {
            type = hypermarketInfo.getString("type");
        } catch (JSONException e) {
            //
        }
        return type;
    }
}
