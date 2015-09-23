package ru.hypernavi.client.app.listeners;

import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;


import android.view.View;
import android.widget.ImageView;
import ru.hypernavi.client.app.AppActivity;

/**
 * Created by Acer on 02.09.2015.
 */
public class ZoomClickListener implements View.OnClickListener {
    private static final Logger LOG = Logger.getLogger(ZoomClickListener.class.getName());
    @NotNull
    private final ImageView myView;
    private final boolean isInClickListener;
    @NotNull
    private final AppActivity myAppActivity;

    public ZoomClickListener(@NotNull final ImageView view, final boolean isInClickListener,
                             @NotNull final AppActivity appActivity)
    {
        myView = view;
        this.isInClickListener = isInClickListener;
        myAppActivity = appActivity;
    }

    @Override
    public void onClick(final View view) {
        final float x = myView.getScaleX();
        final float y = myView.getScaleY();

        myAppActivity.setOffPointButton();

        final float step = 0.5f;
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
