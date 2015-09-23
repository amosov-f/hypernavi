package ru.hypernavi.client.app.listeners;

import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;


import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import ru.hypernavi.client.app.AppActivity;

/**
 * Created by Acer on 14.08.2015.
 */
public class ViewOnTouchListener implements View.OnTouchListener {
    private static final Logger LOG = Logger.getLogger(ViewOnTouchListener.class.getName());
    @NotNull
    private final ImageView myView;
    @NotNull
    private final OrientationEventListener myOrientationEventListener;
    @NotNull
    private final AppActivity myAppActivity;

    float downX;
    float downY;
    int totalX;
    int totalY;

    public ViewOnTouchListener(@NotNull final ImageView imageView, @NotNull final OrientationEventListener orientationEventListener,
                               @NotNull final AppActivity appActivity)
    {
        myView = imageView;
        myOrientationEventListener = orientationEventListener;
        myAppActivity = appActivity;
    }

    private int getMaxScroll(final Bitmap myScheme) {
        return Math.max(myScheme.getWidth(), myScheme.getHeight());
    }

    @Override
    public boolean onTouch(final View view, final MotionEvent event) {
        LOG.info("new Touch");

        myAppActivity.setOffPointButton();

        final Bitmap myScheme = ((BitmapDrawable) myView.getDrawable()).getBitmap();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                moveActionHandler(event, myScheme);
                break;

            default:
                LOG.warning("other event");
        }
        LOG.info("End touch");
        return true;
    }

    private void moveActionHandler(final MotionEvent event, final Bitmap scheme) {
        final int maxRight = getMaxScroll(scheme);
        final int maxLeft = -maxRight;
        final int maxBottom = getMaxScroll(scheme);
        final int maxTop = -maxBottom;

        LOG.info("maxLeft: " + maxLeft + " maxTop: " + maxTop);

        final float currentX = event.getX();
        final float currentY = event.getY();
        int scrollByX = (int) (downX - currentX);
        int scrollByY = (int) (downY - currentY);

        final int realScrollByX = (int) (scrollByX * Math.cos(myOrientationEventListener.getMarketUserAngle()) +
                                         scrollByY * Math.sin(myOrientationEventListener.getMarketUserAngle())
        );
        final int realScrollByY = (int) (scrollByY * Math.cos(myOrientationEventListener.getMarketUserAngle()) -
                                         scrollByX * Math.sin(myOrientationEventListener.getMarketUserAngle())
        );
        myView.scrollBy(realScrollByX, realScrollByY);
        downX = currentX;
        downY = currentY;
    }
}
