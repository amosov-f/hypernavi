package ru.hypernavi.client.app;

import org.jetbrains.annotations.NotNull;


import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

/**
 * Created by Acer on 31.07.2015.
 */
public class ScalingView extends ImageView {
    //public ScalingImageView(final Context context) {
    //    super(context);
    //}

    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;

    public ScalingView(final Context mContext, final AttributeSet attrs){
        super(mContext, attrs);
        //...
        // View code goes here
        //...
        mScaleDetector = new ScaleGestureDetector(mContext, new ScaleListener());
    }

    @Override
    public boolean onTouchEvent(@NotNull final MotionEvent ev) {
        // Let the ScaleGestureDetector inspect all events.
        mScaleDetector.onTouchEvent(ev);
        return true;
    }

    @Override
    public void onDraw(@NotNull final Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.scale(mScaleFactor, mScaleFactor);
        //...
        // onDraw() code goes here
        //...
        canvas.restore();
    }

    private class ScaleListener
        extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(final ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));

            invalidate();
            return true;
        }
    }

}
