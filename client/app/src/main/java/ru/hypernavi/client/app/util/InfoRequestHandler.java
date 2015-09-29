package ru.hypernavi.client.app.util;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

    @NotNull
    private final AppActivity myAppActivity;
    private final SafeLoader myLoader;
    private final CacheWorker myCache;

    private final ExecutorService myExecutorService;

    private String scheme;
    private String host;
    private String path;

    public InfoRequestHandler(@NotNull final AppActivity appActivity, final CacheWorker cache) {
        myExecutorService = Executors.newFixedThreadPool(getNThread(AppActivity.PROPERTIES_SCHEME));

        myAppActivity = appActivity;
        myCache = cache;
        myLoader = new SafeLoader(myExecutorService, myCache);

        getProperties(AppActivity.PROPERTIES_SCHEME);
    }

    public InfoResponse getInfoResponse(final GeoPoint geoPosition) {
        final double lat = geoPosition.getLatitude();
        final double lon = geoPosition.getLongitude();
        LOG.info("GeoPoint coordinates " + "lat: " + lat + "lon: " + lon);
        JSONObject root;

        try {
            root = myLoader.getJSON(lat, lon, scheme, host, path);
        } catch (MalformedURLException ignored) {
            LOG.warning("Can't construct URL for info");
            myAppActivity.writeWarningMessage("Can't load info from Internet");
            root = myCache.loadCachedInfo();
        }

        if (root == null) {
            root = myCache.loadCachedInfo();
            if(root == null) {
                LOG.warning("Can't construct URL for info");
                myAppActivity.writeWarningMessage("Can't load info from Internet");
                return null;
            }
        }
        LOG.info("keep response");
        return InfoResponceSerializer.deserialize(root);
    }

    public Bitmap getClosestSchema(final InfoResponse infoResponse) {
        if (infoResponse == null || infoResponse.getClosestMarkets() == null ||
            infoResponse.getClosestMarkets().size() < 1)
        {
            LOG.warning("No markets in responce");
            return myCache.loadCachedOrDefaultScheme();
        } else {
            try {
                return myLoader.getScheme(scheme, host, infoResponse.getClosestMarkets().get(0).getPath());
            } catch (MalformedURLException e) {
                LOG.warning("Can't construct uri for scheme. " + e.getMessage());
                myAppActivity.writeWarningMessage("Internet disabled!");
                return null;
            }
        }
    }

    private void getProperties(@NotNull final String path) {
        try {
            final Config config = Config.load(path);
            this.scheme = config.getProperty("app.server.info.scheme");
            this.host = config.getProperty("app.server.info.host");
            this.path = config.getProperty("app.server.info.path");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private int getNThread(@NotNull final String path) {
        try {
            final Config config = Config.load(path);
            return config.getInt("app.request.pool.size");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
