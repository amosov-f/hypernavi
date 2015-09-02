package ru.hypernavi.client.app.listeners;

import java.util.logging.Logger;


import android.widget.CompoundButton;
import ru.hypernavi.client.app.AppActivity;

/**
 * Created by Acer on 02.09.2015.
 */
public class CheckedChangeListener implements CompoundButton.OnCheckedChangeListener {
    private static final Logger LOG = Logger.getLogger(CheckedChangeListener.class.getName());

    private final AppActivity myAppActivity;
    private final OrientationEventListener myOrientationEventListener;

    public CheckedChangeListener(final AppActivity appActivity,
                                 final OrientationEventListener orientationEventListener)
    {
        myAppActivity = appActivity;
        myOrientationEventListener = orientationEventListener;
    }

    @Override
    public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
        if (isChecked) {
            myOrientationEventListener.onResume();
            myAppActivity.writeWarningMessage("Карта сориентирована");
        } else {
            myOrientationEventListener.onStandby(0.0f);
            myAppActivity.writeWarningMessage("Ориентация отключена");
        }
    }
}
