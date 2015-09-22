package ru.hypernavi.client.app.listeners;

import java.util.logging.Logger;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import ru.hypernavi.client.app.AppActivity;

/**
 * Created by Acer on 16.09.2015.
 */
public class CompasOnClickListener implements View.OnClickListener {
    private static final Logger LOG = Logger.getLogger(CompasOnClickListener.class.getName());
    private static final String OFF_PATH = "/off_orientation_transparent.png";
    private static final String ON_PATH = "/on_orientation_transparent.png";

    private final AppActivity myAppActivity;
    private final OrientationEventListener myOrientationEventListener;

    private final ImageView myCompasButton;
    private boolean isClicked;

    private final Bitmap offOrientationBitmap;
    private final Bitmap onOrientationBitmap;

    public CompasOnClickListener (final AppActivity appActivity, final OrientationEventListener orientationEventListener,
                                  final ImageView compasButton)
    {
        myAppActivity = appActivity;
        myOrientationEventListener = orientationEventListener;
        myCompasButton = compasButton;
        isClicked = false;
        offOrientationBitmap = BitmapFactory.decodeStream(getClass().getResourceAsStream(OFF_PATH));
        onOrientationBitmap = BitmapFactory.decodeStream(getClass().getResourceAsStream(ON_PATH));
        myCompasButton.setImageBitmap(offOrientationBitmap);
    }


    @Override
    public void onClick(final View v) {
        if ((!isClicked) && (myAppActivity.getHasOrientation())) {
            myOrientationEventListener.onResume();
            myAppActivity.writeWarningMessage("Карта сориентирована");
            myCompasButton.setImageBitmap(onOrientationBitmap);
            isClicked = true;
            LOG.info("Orientation on");
        } else if (!myAppActivity.getHasOrientation()) {
            myAppActivity.writeWarningMessage("Ориентация отсутвует");
        } else {
            myOrientationEventListener.onStandby();
            myAppActivity.writeWarningMessage("Ориентация отключена");
            myCompasButton.setImageBitmap(offOrientationBitmap);
            isClicked = false;
            LOG.info("Orientation off");
        }
    }

    public void moveImageToStratPoint() {
        if (isClicked){
            myOrientationEventListener.onStandby();
            myCompasButton.setImageBitmap(offOrientationBitmap);
            isClicked = false;
            LOG.info("Orientation off");
        }
    }

    public boolean getClicked() {
        return isClicked;
    }
}
