package ru.hypernavi.commons;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.logging.Logger;


import org.apache.commons.io.IOUtils;

/**
 * Created by Acer on 15.08.2015.
 */
//TODO: check and correct nullPointerException
public class RequestString implements Callable<String> {
    private static final Logger LOG = Logger.getLogger(RequestString.class.getName());
    private final URL myUrl;

    public RequestString(final URL url) {
        myUrl = url;
    }

    @Nullable
    @Override
    public String call() throws InterruptedException {
        try {
            final HttpURLConnection myConnection = (HttpURLConnection) (myUrl.openConnection());
            LOG.warning("String is loaded");
            return IOUtils.toString(myConnection.getInputStream(), "UTF-8");
        } catch (IOException e) {
            LOG.warning(e.getMessage());
            LOG.warning("Can't read String from internet");
            return null;
        }
    }
}
