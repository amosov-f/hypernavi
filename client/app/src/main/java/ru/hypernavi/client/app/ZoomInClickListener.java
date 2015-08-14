package ru.hypernavi.client.app;

import java.util.logging.Logger;

import android.view.View;
import android.widget.ImageView;

/**
 * Created by Acer on 14.08.2015.
 */
public class ZoomInClickListener implements View.OnClickListener{
    private static final Logger LOG = Logger.getLogger(ZoomInClickListener.class.getName());
    private final ImageView myView;

    ZoomInClickListener(final ImageView view) {
        myView = view;
    }

    @Override
    public void onClick(final View view) {
        final float maxScale = 4;
        final float step = 0.5f;

        final float x = myView.getScaleX();
        final float y = myView.getScaleY();

        if ((x > maxScale - step) || (y > maxScale - step)) {
            myView.setScaleX(maxScale);
            myView.setScaleY(maxScale);
        } else {
            myView.setScaleX(x + step);
            myView.setScaleY(y + step);
        }

        LOG.warning("Image XScale " + myView.getScaleX());
    }
}
