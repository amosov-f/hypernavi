package ru.hypernavi.client.app;

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
public class RequestToJSON implements Callable<String> {
    private static final Logger LOG = Logger.getLogger(RequestToJSON.class.getName());
    private final double myLat;
    private final double myLon;
    private final String myUrl;

    public RequestToJSON(final double lat, final double lon, final String url) {
        myLat = lat;
        myLon = lon;
        myUrl = url;
    }

    @Nullable
    @Override
    public String call() throws InterruptedException {
        try {
            final HttpURLConnection myConnection = (HttpURLConnection) new URL(myUrl + "?lat=" + myLat + "&lon=" + myLon).openConnection();
            //int code = myConnection.getResponseCode();
            //LOG.warning("response code is " + code);
            //if (code != 200) {
                LOG.warning("JSONString is loaded");
                return IOUtils.toString(myConnection.getInputStream());
            //} else {
                //Thread.sleep(100L);
                 //return call();
            //}
            //final URLConnection myConnection = new URL("http://hypernavi.cloudapp.net/schemainfo?lat="
            //                                           + lat + "&lon=" + lon).openConnection();
            //final URLConnection myConnection = new URL("http://10.0.2.2:8080/schemainfo?lat="
            //                                           + lat + "&lon=" + lon).openConnection();
        } catch (IOException e) {
            LOG.warning(e.getMessage());
            LOG.warning("Can't read JSONString from internet");
            return null;
        }
    }
}
