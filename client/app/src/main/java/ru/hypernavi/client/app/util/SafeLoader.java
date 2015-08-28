package ru.hypernavi.client.app.util;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.*;
import java.util.logging.Logger;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import ru.hypernavi.client.app.AppActivity;
import ru.hypernavi.client.app.RequestBitmap;
import ru.hypernavi.client.app.RequestString;

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

    public JSONObject getJSON(final double lat, final double lon, final String infoURL) throws MalformedURLException {
        final URL requestURL = new URL(infoURL + "?lat=" + lat + "&lon=" + lon);
        LOG.info("infoURL: " + requestURL.toString());
        final RequestString requestString = new RequestString(requestURL);
        LOG.info("JSON request URL" + requestURL.toString());
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
            LOG.warning("Timeout exception");
            throw new RuntimeException(e);
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

    public Bitmap getScheme(final String currentSchemeURL) throws MalformedURLException {
        final RequestBitmap requestBitmap = new RequestBitmap(new URL(currentSchemeURL));
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
