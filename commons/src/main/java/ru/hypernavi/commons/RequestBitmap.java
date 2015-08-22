package ru.hypernavi.commons;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
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
    public Bitmap call() throws Exception {
        try {
            final URLConnection myConnection = myUrl.openConnection();
            return BitmapFactory.decodeStream(myConnection.getInputStream());
        } catch (IOException e) {
            LOG.warning(e.getMessage());
            LOG.warning("Can't read scheme from internet");
            return null;
        }
    }
}
