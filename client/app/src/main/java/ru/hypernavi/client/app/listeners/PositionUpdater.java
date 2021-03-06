package ru.hypernavi.client.app.listeners;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.logging.Logger;


import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.ImageView;
import ru.hypernavi.client.app.AppActivity;
import ru.hypernavi.client.app.util.GeoPointsUtils;
import ru.hypernavi.util.GeoPointImpl;

/**
 * Created by Acer on 31.08.2015.
 */
public class PositionUpdater implements LocationListener {
    private static final Logger LOG = Logger.getLogger(PositionUpdater.class.getName());
    //noinspection MagicNumber
    private static final int FIVE_MINUTES = 5 * 1000 * 60;

    @NotNull
    private final LocationManager manager;
    @NotNull
    private final ImageView myView;
    private Long timeCorrection;

    @NotNull
    private final ButtonOnClickListener buttonOnClickListener;
    @NotNull
    private final AppActivity appActivity;

    public PositionUpdater(@NotNull final LocationManager manager, @NotNull final ImageView imageView,
                           @NotNull final ButtonOnClickListener pointButtonListener, @NotNull final AppActivity appActivity)
    {
        this.manager = manager;
        myView = imageView;
        buttonOnClickListener = pointButtonListener;
        this.appActivity = appActivity;
        this.timeCorrection = null;
    }

    @Override
    public void onLocationChanged(@NotNull final Location location) {
        if (timeCorrection == null) {
            timeCorrection = (new Date()).getTime() - location.getTime();
            LOG.warning("Time correction is " + timeCorrection);
            buttonOnClickListener.setTimeCorrection(timeCorrection);
        }
        LOG.info("onLocationChanged");

        manager.removeUpdates(this);

        final GeoPointImpl geoPosition = GeoPointsUtils.makeGeoPoint(location);
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

    public static boolean isActual(final Location location, final Long timeCorrection) {
        LOG.info("locaction time is " + location.getTime());
        return (location.getTime() + timeCorrection + FIVE_MINUTES > (new Date()).getTime());
    }
}
