package ru.hypernavi.client.app.listeners;

import java.util.logging.Logger;


import android.view.View;
import android.widget.ImageView;

/**
 * Created by Acer on 02.09.2015.
 */
public class ZoomClickListener implements View.OnClickListener {
    private static final Logger LOG = Logger.getLogger(ZoomClickListener.class.getName());
    private final ImageView myView;
    private final boolean isInClickListener;

    public ZoomClickListener(final ImageView view, final boolean isInClickListener) {
        myView = view;
        this.isInClickListener = isInClickListener;
    }

    @Override
    public void onClick(final View view) {
        final float step = 0.5f;
        final float x = myView.getScaleX();
        final float y = myView.getScaleY();

        if (isInClickListener) {
            final float maxScale = 4;

            if ((x > maxScale - step) || (y > maxScale - step)) {
                myView.setScaleX(maxScale);
                myView.setScaleY(maxScale);
            } else {
                myView.setScaleX(x + step);
                myView.setScaleY(y + step);
            }
        } else {
            final float minScale = 1.0f;

            if ((x < minScale + step) || (y < minScale + step)) {
                myView.setScaleX(minScale);
                myView.setScaleY(minScale);
            } else {
                myView.setScaleX(x - step);
                myView.setScaleY(y - step);
            }
        }
        LOG.info("Image XScale " + myView.getScaleX());
    }
}
