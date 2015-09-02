package ru.hypernavi.client.app.util;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Logger;


import android.graphics.Bitmap;
import org.json.JSONObject;
import ru.hypernavi.client.app.AppActivity;
import ru.hypernavi.commons.InfoResponse;
import ru.hypernavi.commons.InfoResponceSerializer;
import ru.hypernavi.util.Config;
import ru.hypernavi.util.GeoPoint;

/**
 * Created by Acer on 31.08.2015.
 */
public class InfoRequestHandler {
    private static final Logger LOG = Logger.getLogger(InfoRequestHandler.class.getName());

    private final AppActivity myAppActivity;
    private final SafeLoader myLoader;
    private final CacheWorker myCache;

    private String infoURL;
    private String schemaURL;

    public InfoRequestHandler(final AppActivity appActivity, final SafeLoader loader, final CacheWorker cache) {
        myAppActivity = appActivity;
        myLoader = loader;
        myCache = cache;

        getProperties(AppActivity.PROPERTIES_SCHEME);
    }

    public Bitmap getSchemaResponce(final GeoPoint geoPosition) {
        final double lat = geoPosition.getLatitude();
        final double lon = geoPosition.getLongitude();
        LOG.info("GeoPoint coordinates " + "lat: " + lat + "lon: " + lon);
        final JSONObject root;
        try {
            root = myLoader.getJSON(lat, lon, infoURL);
        } catch (MalformedURLException ignored) {
            LOG.warning("Can't construct URL for info");
            myAppActivity.writeWarningMessage("Internet disabled!");
            return null;
        }

        if (root == null) {
            LOG.warning("Can't construct URL for info");
            return null;
        }

        final InfoResponse response = InfoResponceSerializer.deserialize(root);
        if (response == null || response.getClosestMarkets() == null || response.getClosestMarkets().size() < 1) {
            LOG.warning("No markets in responce");
            return myCache.loadCachedOrDefaultScheme();
        } else {
            final String schemaFullURL = this.schemaURL + response.getClosestMarkets().get(0).getPath();
            try {
                return myLoader.getScheme(schemaFullURL);
            } catch (MalformedURLException e) {
                LOG.warning("Can't construct url for scheme. " + e.getMessage());
                myAppActivity.writeWarningMessage("Internet disabled!");
                return null;
            }
        }
    }

    private void getProperties(@NotNull final String path) {
        try {
            final Config config = Config.load(path);
            infoURL = config.getProperty("app.server.info.host");
            schemaURL = config.getProperty("app.server.schema.host");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
