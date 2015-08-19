package ru.hypernavi.client.app;

import java.util.Date;
import java.util.logging.Logger;


import android.location.Location;
import android.location.LocationManager;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Acer on 19.08.2015.
 */
public class ButtonOnClickListener implements View.OnClickListener {
    private static final int ONE_MINUTES = 1000 * 60 * 1;
    private static final Logger LOG = Logger.getLogger(ButtonOnClickListener.class.getName());
    private final LocationManager myLocationManager;
    private final ImageView myImageView;
    private final Long myTimeCorrection;
    private final AppActivity myAppActivity;

    public ButtonOnClickListener(final LocationManager locationManager, final ImageView imageView,
                                 final Long timeCorrection, final AppActivity appActivity) {
        myLocationManager = locationManager;
        myImageView = imageView;
        myTimeCorrection = timeCorrection;
        myAppActivity = appActivity;
    }

    @Override
    public void onClick(final View v) {
        final Location location = myLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        final long locationTime = location.getTime() + myTimeCorrection;
        if (locationTime + ONE_MINUTES < (new Date()).getTime()) {
            myAppActivity.sendRequest(myImageView);
        }
        LOG.warning("location time is " + (location.getTime() + myTimeCorrection));
        final Date date = new Date();
        LOG.warning("util time is     " + date.getTime());
    }
}
