package ru.hypernavi.client.app.util;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.logging.Logger;


import android.net.Uri;
import org.apache.commons.io.IOUtils;

/**
 * Created by Acer on 15.08.2015.
 */
public class RequestString implements Callable<String> {
    private static final Logger LOG = Logger.getLogger(RequestString.class.getName());

    private final Uri myUri;

    public RequestString(final Uri uri) {
        myUri = uri;
    }

    @Nullable
    @Override
    public String call() throws InterruptedException {
        try {
            final URL url = new URL(myUri + "");
            final HttpURLConnection myConnection = (HttpURLConnection) (url.openConnection());
            if (myConnection == null) {
                return null;
            }
            if (myConnection.getResponseCode() != myConnection.HTTP_OK) {
                LOG.warning("Response code is " + myConnection.getResponseCode());
                return null;
            } else {
                LOG.warning("String is loaded");
                final java.io.InputStream inputStream = myConnection.getInputStream();
                if (inputStream == null) {
                    return null;
                }
                return IOUtils.toString(inputStream);
            }
        } catch (IOException e) {
            LOG.warning(e.getMessage());
            LOG.warning("Can't read String from internet");
            return null;
        }
    }
}
