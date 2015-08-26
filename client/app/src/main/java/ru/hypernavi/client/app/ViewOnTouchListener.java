package ru.hypernavi.client.app;

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

    private final int myDisplayWidth;
    private final int myDisplayHeight;
    float downX;
    float downY;
    int totalX;
    int totalY;

    public ViewOnTouchListener(final int displayWidth, final int displayHeight, @NotNull final ImageView imageView) {
        myDisplayWidth = displayWidth;
        myDisplayHeight = displayHeight;
        myView = imageView;
    }

    private int getMaxXScroll(final Bitmap myScheme) {
        //noinspection MagicNumber
        return (int) (2.0f * Math.abs((Math.max(myScheme.getWidth(), myScheme.getHeight()) / 2.0f) -
                                      (Math.min(myDisplayWidth, myDisplayHeight) / myView.getScaleX() / 2.0f)));
    }

    private int getMaxYScroll(final Bitmap myScheme) {
        //noinspection MagicNumber
        return (int) (2.0f * Math.abs((Math.max(myScheme.getWidth(), myScheme.getHeight()) / 2.0f) -
                                       (Math.min(myDisplayWidth, myDisplayHeight) / myView.getScaleY() / 2.0f)));
    }



    private int newScroll(final boolean inBorder, final boolean notReachedMax, final int max, final int total, final int scroll) {
        if (inBorder) {
            if (total == max) {
                return 0;
            } else if (notReachedMax) {
                return scroll;
            } else {
                return max - (total - scroll);
            }
        }
        return scroll;
    }

    private int newTotal(final boolean inBorder, final boolean notReachedMax, final int max, final int total, final int scroll) {
        if (inBorder) {
            if (total == max) {
                return total;
            } else if (notReachedMax) {
                return total + scroll;
            } else {
                return max;
            }
        }
        return total;
    }

    @Override
    public boolean onTouch(final View view, final MotionEvent event) {
        LOG.info("new Touch");
        final Bitmap myScheme = ((BitmapDrawable) myView.getDrawable()).getBitmap();

        final int maxRight = getMaxXScroll(myScheme);
        final int maxLeft = -maxRight;
        final int maxBottom = getMaxYScroll(myScheme);
        final int maxTop = -maxBottom;

        LOG.info("maxLeft: " + maxLeft + " maxTop: " + maxTop);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                break;

            case MotionEvent.ACTION_MOVE:
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

                myView.scrollBy(scrollByX, scrollByY);
                downX = currentX;
                downY = currentY;
                break;
            default:
                LOG.warning("other event");
        }
        LOG.info("End touch");
        return true;
    }
}
