package ru.hypernavi.client.app;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.logging.Logger;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by Acer on 15.08.2015.
 */
public class RequestBitmap implements Callable<Bitmap> {
    private static final Logger LOG = Logger.getLogger(RequestBitmap.class.getName());
    private URL myUrl;

    public RequestBitmap(final URL url) {
        myUrl = url;
    }

    @Nullable
    @Override
    public Bitmap call() {
        try {
            final HttpURLConnection myConnection = (HttpURLConnection) (myUrl.openConnection());
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
