package ru.hypernavi.client.app.listeners;

import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;


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

    @Override
    public boolean onTouch(final View view, final MotionEvent event) {
        LOG.info("new Touch");

        myAppActivity.setOffPointButton();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                moveActionHandler(event);
                break;

            default:
                LOG.warning("other event");
        }
        LOG.info("End touch");
        return true;
    }

    private void moveActionHandler(final MotionEvent event) {

        final float currentX = event.getX();
        final float currentY = event.getY();
        final int scrollByX = (int) (downX - currentX);
        final int scrollByY = (int) (downY - currentY);

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
