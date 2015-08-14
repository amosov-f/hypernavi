package ru.hypernavi.client.app;

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
    private final ImageView myView;
    private final int myDisplayWidth;
    private final int myDisplayHeight;
    float downX;
    float downY;
    int totalX;
    int totalY;
    int scrollByX;
    int scrollByY;

    public ViewOnTouchListener(final int displayWidth, final int displayHeight, final ImageView imageView) {
        myDisplayWidth = displayWidth;
        myDisplayHeight = displayHeight;
        myView = imageView;
    }

    @Override
    public boolean onTouch(final View view, final MotionEvent event) {
        LOG.warning("new Touch");
        final Bitmap myScheme = ((BitmapDrawable)myView.getDrawable()).getBitmap();
        final int maxRight = Math.abs((myScheme.getWidth() / 2) - (myDisplayWidth / 2));
        final int maxLeft = -maxRight;
        final int maxBottom = Math.abs((myScheme.getHeight() / 2) - (myDisplayHeight / 2));
        final int maxTop = -maxBottom;

        LOG.info("maxLeft: " + maxLeft + " maxTop: " + maxTop );
        final float currentX;
        final float currentY;
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                currentX = event.getX();
                currentY = event.getY();
                scrollByX = (int)(downX - currentX);
                scrollByY = (int)(downY - currentY);

                // scrolling to left side of image (pic moving to the right)
                if (currentX > downX) {
                    if (totalX == maxLeft) {
                        scrollByX = 0;
                    }
                    if (totalX > maxLeft) {
                        totalX = totalX + scrollByX;
                    }
                    if (totalX < maxLeft) {
                        scrollByX = maxLeft - (totalX - scrollByX);
                        totalX = maxLeft;
                    }
                }

                // scrolling to right side of image (pic moving to the left)
                if (currentX < downX) {
                    if (totalX == maxRight) {
                        scrollByX = 0;
                    }
                    if (totalX < maxRight) {
                        totalX = totalX + scrollByX;
                    }
                    if (totalX > maxRight) {
                        scrollByX = maxRight - (totalX - scrollByX);
                        totalX = maxRight;
                    }
                }

                // scrolling to top of image (pic moving to the bottom)
                if (currentY > downY) {
                    if (totalY == maxTop) {
                        scrollByY = 0;
                    }
                    if (totalY > maxTop) {
                        totalY = totalY + scrollByY;
                    }
                    if (totalY < maxTop) {
                        scrollByY = maxTop - (totalY - scrollByY);
                        totalY = maxTop;
                    }
                }

                // scrolling to bottom of image (pic moving to the top)
                if (currentY < downY) {
                    if (totalY == maxBottom) {
                        scrollByY = 0;
                    }
                    if (totalY < maxBottom) {
                        totalY = totalY + scrollByY;
                    }
                    if (totalY > maxBottom) {
                        scrollByY = maxBottom - (totalY - scrollByY);
                        totalY = maxBottom;
                    }
                }

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
