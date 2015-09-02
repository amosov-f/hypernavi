package ru.hypernavi.client.app.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.logging.Logger;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

/**
 * Created by Acer on 15.08.2015.
 */
// TODO amosov-f: create base class for request callable
public class RequestBitmap implements Callable<Bitmap> {
    private static final Logger LOG = Logger.getLogger(RequestBitmap.class.getName());
    @NotNull
    private final Uri myUri;

    public RequestBitmap(@NotNull final Uri uri) {
        myUri = uri;
    }

    @Nullable
    @Override
    public Bitmap call() {
        try {
            final URL url = new URL(myUri + "");
            final HttpURLConnection myConnection = (HttpURLConnection) (url.openConnection());
            myConnection.getResponseCode();
            if (myConnection.getResponseCode() != myConnection.HTTP_OK) {
                LOG.warning("Response code is " + myConnection.getResponseCode());
                return null;
            } else {
                LOG.info("Bitmap is loaded");
                return BitmapFactory.decodeStream(myConnection.getInputStream());
            }
        } catch (IOException e) {
            LOG.warning(e.getMessage());
            LOG.warning("Can't read scheme from internet");
            return null;
        }
    }
}
