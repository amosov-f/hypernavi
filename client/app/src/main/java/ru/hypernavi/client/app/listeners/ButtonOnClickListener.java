package ru.hypernavi.client.app.listeners;

import java.util.Date;
import java.util.logging.Logger;

import android.location.Location;
import android.location.LocationManager;
import android.view.View;
import ru.hypernavi.client.app.AppActivity;

/**
 * Created by Acer on 19.08.2015.
 */
public class ButtonOnClickListener implements View.OnClickListener {

    private static final Logger LOG = Logger.getLogger(ButtonOnClickListener.class.getName());
    private final LocationManager myLocationManager;
    private final Long myTimeCorrection;
    private final AppActivity myAppActivity;

    public ButtonOnClickListener(final LocationManager locationManager, final Long timeCorrection,
                                 final AppActivity appActivity)
    {
        myLocationManager = locationManager;
        myTimeCorrection = timeCorrection;
        myAppActivity = appActivity;
    }

    @Override
    public void onClick(final View v) {
        // TODO amosov-f: move location request code to one place
        Location location = myLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (!PositionUpdater.isActual(location, myTimeCorrection)) {
            location = myLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (!PositionUpdater.isActual(location, myTimeCorrection)) {
                LOG.info("send new request to update position");
                myAppActivity.sendLocationRequest();
            }
        }
        LOG.info("location time is " + (location.getTime() + myTimeCorrection));
        LOG.info("util time is     " + (new Date()).getTime());
    }


}
