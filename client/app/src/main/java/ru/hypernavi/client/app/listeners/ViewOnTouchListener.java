package ru.hypernavi.client.app.listeners;

import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;


import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Acer on 14.08.2015.
 */
public class ViewOnTouchListener implements View.OnTouchListener {
    private static final Logger LOG = Logger.getLogger(ViewOnTouchListener.class.getName());
    @NotNull
    private final ImageView myView;
    @NotNull
    private final OrientationEventListener myOrientationEventListener;

    float downX;
    float downY;
    int totalX;
    int totalY;

    public ViewOnTouchListener(@NotNull final ImageView imageView, @NotNull final OrientationEventListener orientationEventListener /*@NotNull final AppActivity appActivity*/) {
        myView = imageView;
        myOrientationEventListener = orientationEventListener;
    }

    private int getMaxScroll(final Bitmap myScheme) {
        return Math.max(myScheme.getWidth(), myScheme.getHeight());
    }

    private int newScroll(final boolean inBorder, final boolean notReachedMax, final int max, final int total, final int scroll) {
        return inBorder ? total == max ? 0 : notReachedMax ? scroll : max - (total - scroll) : scroll;
    }

    private int newTotal(final boolean inBorder, final boolean notReachedMax, final int max, final int total, final int scroll) {
        return inBorder ? total == max ? total : notReachedMax ? total + scroll : max : total;
    }

    @Override
    public boolean onTouch(final View view, final MotionEvent event) {
        LOG.info("new Touch");
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

        // scrolling to left side of image (pic moving to the right)
        scrollByX = newScroll(currentX > downX, totalX > maxLeft, maxLeft, totalX, scrollByX);
        totalX = newTotal(currentX > downX, totalX > maxLeft, maxLeft, totalX, scrollByX);

        // scrolling to right side of image (pic moving to the left)
        scrollByX = newScroll(currentX < downX, totalX < maxRight, maxRight, totalX, scrollByX);
        totalX = newTotal(currentX < downX, totalX < maxRight, maxRight, totalX, scrollByX);

        // scrolling to top of image (pic moving to the bottom)
        scrollByY = newScroll(currentY > downY, totalY > maxTop, maxTop, totalY, scrollByY);
        totalY = newTotal(currentY > downY, totalY > maxTop, maxTop, totalY, scrollByY);

        // scrolling to bottom of image (pic moving to the top)
        scrollByY = newScroll(currentY < downY, totalY < maxBottom, maxBottom, totalY, scrollByY);
        totalY = newTotal(currentY < downY, totalY < maxBottom, maxBottom, totalY, scrollByY);

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
