package ru.hypernavi.client.app;

import java.util.logging.Logger;


import android.view.View;
import android.widget.ImageView;

/**
 * Created by Acer on 14.08.2015.
 */
// TODO amosov-f: union with ZoomInClickListener
public class ZoomOutClickListener implements View.OnClickListener {
    private static final Logger LOG = Logger.getLogger(ZoomOutClickListener.class.getName());
    private final ImageView myView;

    ZoomOutClickListener(final ImageView view) {
        myView = view;
    }

    @Override
    public void onClick(final View view) {
        final float minScale = 1.0f;
        final float step = 0.5f;

        final float x = myView.getScaleX();
        final float y = myView.getScaleY();

        if ((x < minScale + step) || (y < minScale + step)) {
            myView.setScaleX(minScale);
            myView.setScaleY(minScale);
        } else {
            myView.setScaleX(x - step);
            myView.setScaleY(y - step);
        }

        LOG.warning("Image XScale " + myView.getScaleX());
    }
}
