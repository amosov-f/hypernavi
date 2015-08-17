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
        return Math.abs((myScheme.getWidth() / 2) - (myDisplayWidth / 2));
    }

    private int getMaxYScroll(final Bitmap myScheme) {
        return Math.abs((myScheme.getHeight() / 2) - (myDisplayHeight / 2));
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
                scrollByX = newScroll(currentX > downX, totalX > maxLeft, totalX, maxLeft, scrollByX);
                totalX = newTotal(currentX > downX, totalX > maxLeft, totalX, maxLeft, scrollByX);
                /*if (currentX > downX) {
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
                }*/

                // scrolling to right side of image (pic moving to the left)
                scrollByX = newScroll(currentX < downX, totalX < maxRight, totalX, maxRight, scrollByX);
                totalX = newTotal(currentX < downX, totalX < maxRight, totalX, maxRight, scrollByX);
                /*
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
                */
                // scrolling to top of image (pic moving to the bottom)
                scrollByY = newScroll(currentY > downY, totalY > maxTop, totalY, maxTop, scrollByY);
                totalY = newTotal(currentY > downY, totalY > maxTop, totalY, maxTop, scrollByY);

                /*if (currentY > downY) {
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
                }*/

                // scrolling to bottom of image (pic moving to the top)
                scrollByY = newScroll(currentY < downY, totalY < maxBottom, totalY, maxBottom, scrollByY);
                totalY = newTotal(currentY < downY, totalY < maxBottom, totalY, maxBottom, scrollByY);
                /*
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
                */
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
