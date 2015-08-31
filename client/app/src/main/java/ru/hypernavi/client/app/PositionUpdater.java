package ru.hypernavi.client.app;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.logging.Logger;


import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import ru.hypernavi.client.app.util.GeoPointsUtils;
import ru.hypernavi.util.GeoPoint;

/**
 * Created by Acer on 31.08.2015.
 */
public class PositionUpdater implements LocationListener {
    private static final Logger LOG = Logger.getLogger(PositionUpdater.class.getName());
    private static final int FIFTEEN_MINUTES = 15 * 1000 * 60;

    @NotNull
    private final LocationManager manager;
    @NotNull
    private final ImageView myView;
    private Long timeCorrection;

    private final Button button;
    private final AppActivity appActivity;

    PositionUpdater(@NotNull final LocationManager manager, @NotNull final ImageView imageView,
                    @NotNull final Button button, @NotNull final AppActivity appActivity)
    {
        this.manager = manager;
        myView = imageView;
        this.button = button;
        this.appActivity = appActivity;
        this.timeCorrection = null;
    }

    @Override
    public void onLocationChanged(@NotNull final Location location) {

        if (timeCorrection == null) {
            timeCorrection = (new Date()).getTime() - location.getTime();
            LOG.warning("Time correction is " + timeCorrection);
            registerButtonListener(manager);
        }
        LOG.info("onLocationChanged");

        manager.removeUpdates(this);

        final GeoPoint geoPosition = GeoPointsUtils.makeGeoPoint(location);
        appActivity.processInfo(geoPosition, myView);
    }

    @Override
    public void onStatusChanged(@NotNull final String provider, final int status, @NotNull final Bundle extras) {
    }

    @Override
    public void onProviderEnabled(@NotNull final String provider) {
    }

    @Override
    public void onProviderDisabled(@NotNull final String provider) {
    }

    private void registerButtonListener(final LocationManager locationManager) {
        final ButtonOnClickListener buttonOnClickListener = new ButtonOnClickListener(locationManager, timeCorrection, appActivity);
        button.setOnClickListener(buttonOnClickListener);
    }

    public static boolean isActual(final Location location, final Long timeCorrection) {
        LOG.info("locaction time is " + location.getTime());
        return (location.getTime() + timeCorrection + FIFTEEN_MINUTES > (new Date()).getTime());
    }
}
