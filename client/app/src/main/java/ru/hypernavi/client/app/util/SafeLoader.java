package ru.hypernavi.client.app.util;

import java.net.MalformedURLException;
import java.util.concurrent.*;
import java.util.logging.Logger;


import android.graphics.Bitmap;
import android.net.Uri;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Константин on 28.08.2015.
 */
public class SafeLoader {
    private static final Logger LOG = Logger.getLogger(SafeLoader.class.getName());
    private final ExecutorService executorService;
    private static final long MAX_TIME_OUT = 5000L;
    private final CacheWorker cache;

    public SafeLoader(final ExecutorService executorService, final CacheWorker cache) {
        this.executorService = executorService;
        this.cache = cache;
    }

    public JSONObject getJSON(final double lat, final double lon,
                              final String scheme, final String host, final String path) throws MalformedURLException
    {
        final Uri requestURI = new Uri.Builder()
            .scheme(scheme)
            .authority(host)
            .appendPath(path)
            .appendQueryParameter("lat", Double.toString(lat))
            .appendQueryParameter("lon", Double.toString(lon))
            .build();
        LOG.info("JSON request URI" + requestURI);

        final RequestString requestString = new RequestString(requestURI);
        final Future<String> task = executorService.submit(requestString);
        String hypermarketsJSON;
        try {
            hypermarketsJSON = task.get(MAX_TIME_OUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            LOG.warning(e.getMessage());
            hypermarketsJSON = null;
        } catch (ExecutionException e) {
            LOG.warning("Execution exception");
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            LOG.warning("Timeout exception " + e);
            hypermarketsJSON = null;
        }

        if (hypermarketsJSON == null) {
            LOG.warning("JSON string is null");
            return null;
        }
        try {
            return new JSONObject(hypermarketsJSON);
        } catch (JSONException e) {
            LOG.warning(e.getMessage());
            return null;
        }
    }

    public Bitmap getScheme(final String scheme, final String host, final String path) throws MalformedURLException {
        final Uri requestURI = new Uri.Builder()
            .scheme(scheme)
            .authority(host)
            .appendPath(path.substring(1))
            .build();
        LOG.info("JSON request URI" + requestURI);

        final RequestBitmap requestBitmap = new RequestBitmap(requestURI);
        final Future<Bitmap> task = executorService.submit(requestBitmap);
        try {
            final Bitmap answer = task.get(MAX_TIME_OUT, TimeUnit.MILLISECONDS);
            if (answer == null) {
                return cache.loadCachedOrDefaultScheme();
            } else {
                return answer;
            }
        } catch (InterruptedException e) {
            LOG.warning(e.getMessage());
            return cache.loadCachedOrDefaultScheme();
        } catch (ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}
