package ru.hypernavi.client.app;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;
import java.util.logging.Logger;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by Acer on 15.08.2015.
 */
public class RequestToScheme implements Callable<Bitmap> {
    private static final Logger LOG = Logger.getLogger(RequestToScheme.class.getName());
    private URL myUrl;

    public RequestToScheme(final String infoURL) {
        myUrl = null;
        try {
            myUrl = new URL(infoURL);
        } catch (MalformedURLException e) {
            LOG.warning("can't construct URL for scheme");
            //e.printStackTrace();
        }
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
            return  null;
        }
    }
}
