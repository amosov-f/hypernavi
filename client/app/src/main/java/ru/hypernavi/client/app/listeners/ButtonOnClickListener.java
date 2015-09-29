package ru.hypernavi.client.app.listeners;

import java.util.Date;
import java.util.logging.Logger;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.view.View;
import android.widget.ImageView;
import ru.hypernavi.client.app.AppActivity;
import ru.hypernavi.client.app.R;

/**
 * Created by Acer on 19.08.2015.
 */
public class ButtonOnClickListener implements View.OnClickListener {
    private static final String OFF_POINT_PATH = "/off_location_icon.png";
    private static final String ON_POINT_PATH = "/on_location_icon.png";
    private static final Logger LOG = Logger.getLogger(ButtonOnClickListener.class.getName());
    private final LocationManager myLocationManager;
    private Long myTimeCorrection;
    private final AppActivity myAppActivity;
    private final ImageView myPointButton;
    private boolean isClicked;

    private final Bitmap offOrientationBitmap;
    private final Bitmap onOrientationBitmap;

    public ButtonOnClickListener(final LocationManager locationManager,
                                 final AppActivity appActivity)
    {
        myLocationManager = locationManager;
        myTimeCorrection = null;
        myAppActivity = appActivity;
        offOrientationBitmap = BitmapFactory.decodeStream(getClass().getResourceAsStream(OFF_POINT_PATH));
        onOrientationBitmap = BitmapFactory.decodeStream(getClass().getResourceAsStream(ON_POINT_PATH));
        myPointButton = (ImageView) myAppActivity.findViewById(R.id.pointButton);
        myPointButton.setImageBitmap(offOrientationBitmap);
        isClicked = false;
        myPointButton.setOnClickListener(this);
    }

    public void setTimeCorrection(final Long timeCorrection) {
        myTimeCorrection = timeCorrection;
    }

    @Override
    public void onClick(final View v) {
        myAppActivity.moveImageToStartPoint();
        myPointButton.setImageBitmap(onOrientationBitmap);
        isClicked = true;
        if (myTimeCorrection == null) {
            return;
        }
        // TODO amosov-f: move location request code to one place
        Location location = myLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (!PositionUpdater.isActual(location, myTimeCorrection)) {
            location = myLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (!PositionUpdater.isActual(location, myTimeCorrection)) {
                LOG.info("send new request to update position");
                myAppActivity.sendLocationRequest();
            } else if (!myAppActivity.haveInfoResponse()) {
                myAppActivity.writeWarningMessage("Internet disabled!");
                myAppActivity.sendLocationRequest();

            }
        } else if (!myAppActivity.haveInfoResponse()) {
            myAppActivity.writeWarningMessage("Internet disabled!");
            myAppActivity.sendLocationRequest();
        }

        LOG.info("location time is " + (location.getTime() + myTimeCorrection));
        LOG.info("util time is     " + (new Date()).getTime());
    }

    public void setOffPointButton() {
        if (isClicked) {
            isClicked = false;
            myPointButton.setImageBitmap(offOrientationBitmap);
        }
    }
}
