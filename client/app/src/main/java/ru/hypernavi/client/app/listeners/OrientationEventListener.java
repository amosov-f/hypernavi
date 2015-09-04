package ru.hypernavi.client.app.listeners;

import java.util.Date;
import java.util.logging.Logger;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import ru.hypernavi.client.app.AppActivity;

/**
 * Created by Acer on 31.08.2015.
 */
public class OrientationEventListener implements SensorEventListener {
    private static final Logger LOG = Logger.getLogger(OrientationEventListener.class.getName());
    private static final float PI_IN_DEGREES = (float) Math.toDegrees(Math.PI);

    private static final long HALF_A_SECOND = 500000000L;

    private final ImageView myImageView;
    private final AppActivity myAppActivity;

    private float userAzimuthInDegrees;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private boolean lastAccelerometerSet = false;
    private boolean lastMagnetometerSet = false;
    private final float[] lastAccelerometer = new float[3];
    private final float[] lastMagnetometer = new float[3];
    private long timeStamp;

    private boolean isFirstSensorChangedWithThisMarket;
    private float mapAzimuthInDegrees;

    public OrientationEventListener(final ImageView imageView, final AppActivity appActivity) {
        myImageView = imageView;
        myAppActivity = appActivity;
        sensorManager = (SensorManager) myAppActivity.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        timeStamp = (new Date()).getTime();
        isFirstSensorChangedWithThisMarket = true;
    }

    @Override
    public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
    }

    public void onResume() {
        // for the system's orientation sensor registered listeners
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
    }

    public void onPause() {
        // to stop the listener and save battery
        sensorManager.unregisterListener(this);
        isFirstSensorChangedWithThisMarket = true;
    }

    public void onStandby() {
        if (!isFirstSensorChangedWithThisMarket) {
            final RotateAnimation rotateAnimation = prepareAnimation(userAzimuthInDegrees - mapAzimuthInDegrees, 0);

            myImageView.startAnimation(rotateAnimation);
        }
        onPause();
    }

    @Override
    public void onSensorChanged(final SensorEvent event) {
        if (timeStamp + HALF_A_SECOND > event.timestamp) {
            return;
        }
        // get the angle around the z-axis rotated
        if (event.sensor == accelerometer) {
            System.arraycopy(event.values, 0, lastAccelerometer, 0, event.values.length);
            lastAccelerometerSet = true;
        } else if (event.sensor == magnetometer) {
            System.arraycopy(event.values, 0, lastMagnetometer, 0, event.values.length);
            lastMagnetometerSet = true;
        }
        if (lastAccelerometerSet && lastMagnetometerSet) {
            final float marketAzimuthInDegrees = myAppActivity.getCurrentMarketAzimuthInDegrees();
            if (isFirstSensorChangedWithThisMarket) {
                LOG.info("first orientation");
                isFirstSensorChangedWithThisMarket = false;
                mapAzimuthInDegrees = executeUserAzimuthInDegrees();
                //noinspection MagicNumber
                if (inEpsilonSphere(mapAzimuthInDegrees, marketAzimuthInDegrees, 20)) {
                    timeStamp = event.timestamp;
                    return;
                }
                final RotateAnimation rotateAnimation = prepareAnimation(0, marketAzimuthInDegrees - mapAzimuthInDegrees);

                myImageView.startAnimation(rotateAnimation);
                timeStamp = event.timestamp;
                userAzimuthInDegrees = mapAzimuthInDegrees;
            } else {
                final float newUserAzimuthInDegrees = executeUserAzimuthInDegrees();
                //noinspection MagicNumber
                if ((inEpsilonSphere(userAzimuthInDegrees, newUserAzimuthInDegrees, 20))) {
                    timeStamp = event.timestamp;
                    return;
                }

                final RotateAnimation rotateAnimation = prepareAnimation(marketAzimuthInDegrees - userAzimuthInDegrees,
                    marketAzimuthInDegrees - newUserAzimuthInDegrees);

                myImageView.startAnimation(rotateAnimation);
                timeStamp = event.timestamp;
                userAzimuthInDegrees = newUserAzimuthInDegrees;
            }
        }
    }

    private float executeUserAzimuthInDegrees() {
        lastAccelerometerSet = false;
        lastMagnetometerSet = false;
        final float[] R = new float[9];
        SensorManager.getRotationMatrix(R, null, lastAccelerometer, lastMagnetometer);
        final float[] orientation = new float[3];
        SensorManager.getOrientation(R, orientation);
        // create a rotation animation (reverse turn degree degrees)
        //LOG.info("orientation: " + Math.toDegrees(orientation[0]) + " " + Math.toDegrees(orientation[1])
        //         + " " + Math.toDegrees(orientation[2]));

        //noinspection MagicNumber
        return (float) (Math.toDegrees(orientation[0]) + 180) % 360;
    }

    private RotateAnimation prepareAnimation(final float azimuthFrom, final float azimuthTo) {
        final RotateAnimation rotateAnimation;
        //noinspection MagicNumber
        if (Math.abs(azimuthTo - azimuthFrom) > PI_IN_DEGREES) {
            if (azimuthFrom < azimuthTo) {
                //noinspection MagicNumber
                rotateAnimation = new RotateAnimation(
                    azimuthFrom,
                    azimuthTo - 2 * PI_IN_DEGREES,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f);
            } else {
                //noinspection MagicNumber
                rotateAnimation = new RotateAnimation(
                    azimuthFrom - 2 * PI_IN_DEGREES,
                    azimuthTo,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f);
            }
        } else {
            //noinspection MagicNumber
            rotateAnimation = new RotateAnimation(
                azimuthFrom,
                azimuthTo,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);
        }

        //noinspection MagicNumber
        rotateAnimation.setDuration(500);
        rotateAnimation.setFillAfter(true);

        return rotateAnimation;
    }

    private boolean inEpsilonSphere(final float x, final float sphereCenter, final float delta) {
        return (Math.abs(x - sphereCenter) < delta);
    }

    public float getMarketUserAngle() {
        if (isFirstSensorChangedWithThisMarket) {
            return 0;
        } else {
            return (float) Math.toRadians(myAppActivity.getCurrentMarketAzimuthInDegrees() - userAzimuthInDegrees);
        }
    }

    public void setFlag(final boolean flag) {
        isFirstSensorChangedWithThisMarket = flag;
    }

}
