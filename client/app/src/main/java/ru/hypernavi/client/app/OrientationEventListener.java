package ru.hypernavi.client.app;

import java.util.Date;
import java.util.logging.Logger;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

/**
 * Created by Acer on 31.08.2015.
 */
public class OrientationEventListener implements SensorEventListener {
    private static final Logger LOG = Logger.getLogger(OrientationEventListener.class.getName());
    private static final long HALF_A_SECOND = 500000000L;

    private final ImageView myImageView;
    private final AppActivity myAppActivity;

    private float currentDegree = 0f;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private boolean lastAccelerometerSet = false;
    private boolean lastMagnetometerSet = false;
    private final float[] lastAccelerometer = new float[3];
    private final float[] lastMagnetometer = new float[3];
    private long timeStamp;

    public OrientationEventListener(final ImageView imageView, final AppActivity appActivity) {
        myImageView = imageView;
        myAppActivity = appActivity;
        sensorManager = (SensorManager) myAppActivity.getSystemService(myAppActivity.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        timeStamp = (new Date()).getTime();
    }

    @Override
    public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
    }

    protected void onResume() {
        //super.onResume();
        // for the system's orientation sensor registered listeners
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
    }

    protected void onPause() {
        //super.onPause();
        // to stop the listener and save battery
        sensorManager.unregisterListener(this);
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
            lastAccelerometerSet = false;
            lastMagnetometerSet = false;
            final float[] R = new float[9];
            SensorManager.getRotationMatrix(R, null, lastAccelerometer, lastMagnetometer);
            final float[] orientation = new float[3];
            SensorManager.getOrientation(R, orientation);
            final float azimuthInDegress = (float) (Math.toDegrees(orientation[0]) + 360) % 360;
            // create a rotation animation (reverse turn degree degrees)
            LOG.info("orientation: " + Math.toDegrees(orientation[0]) + " " + Math.toDegrees(orientation[1])
                     + " " + Math.toDegrees(orientation[2]));
            timeStamp = event.timestamp;

            //LOG.info("rotation parameters: " + currentDegree + " " + -azimuthInDegress);

            if ((inEpsilonSphere(currentDegree + azimuthInDegress, 360, 20)) ||
                (inEpsilonSphere(currentDegree + azimuthInDegress, 0, 20)))
            {
                timeStamp = event.timestamp;
                return;
            }

            final RotateAnimation ra;
            if (Math.abs(currentDegree + azimuthInDegress) > 180) {
                if (currentDegree < -azimuthInDegress) {
                    ra = new RotateAnimation(
                        currentDegree,
                        -azimuthInDegress - 360,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f);
                } else {
                    ra = new RotateAnimation(
                        currentDegree,
                        -azimuthInDegress + 360,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f);
                }
            } else {
                ra = new RotateAnimation(
                    currentDegree,
                    -azimuthInDegress,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f);
            }
            ra.setDuration(350);
            ra.setFillAfter(true);

            myImageView.startAnimation(ra);
            currentDegree = -azimuthInDegress;
            timeStamp = event.timestamp;
        }
    }

    public float getCurrentDegreeInRadian() {
        return (float) Math.toRadians(currentDegree);
    }

    private boolean inEpsilonSphere(final float x, final float sphereCenter, final float delta) {
        return (Math.abs(x - sphereCenter) < delta);
    }
}
