package ru.hypernavi.client.app;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Display;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ZoomControls;
import org.json.JSONObject;
import ru.hypernavi.client.app.util.CacheWorker;
import ru.hypernavi.client.app.util.GeoPoints;
import ru.hypernavi.client.app.util.SafeLoader;
import ru.hypernavi.commons.InfoResponce;
import ru.hypernavi.commons.InfoResponceSerializer;
import ru.hypernavi.util.Config;
import ru.hypernavi.util.GeoPoint;

// TODO move sensor event listening to separate class
public final class AppActivity extends Activity implements SensorEventListener {
    private static final Logger LOG = Logger.getLogger(AppActivity.class.getName());
    private static final String PROPERTIES_SCHEME = "classpath:/app-common.properties";
    // TODO amosov-f: WTF?!?
    private static final long THREE_SECONDS = 300000000L;
    // TODO amosov-f: WTF?!?
    private static final int FIVETEEN_MINUTES = 15 * 1000 * 60;

    private Bitmap originScheme;
    private int displayWidth;
    private int displayHeight;

    private ImageView imageView;

    private int nThread;
    private ExecutorService executorService;
    private String infoURL;
    private String schemaURL;

    private LocationManager locationManager;
    private Long timeCorrection;

    private float currentDegree = 0f;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private boolean lastAccelerometerSet = false;
    private boolean lastMagnetometerSet = false;
    private final float[] lastAccelerometer = new float[3];
    private final float[] lastMagnetometer = new float[3];
    private long timeStamp = (new Date()).getTime();

    private SafeLoader loader;
    private CacheWorker cache;

    @Nullable
    private volatile Location location;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LOG.info("onCreate start");

        setContentView(R.layout.main);
        imageView = (ImageView) findViewById(R.id.imageView);

        getProperties(PROPERTIES_SCHEME);
        executorService = Executors.newFixedThreadPool(nThread);

        cache = new CacheWorker(this);
        loader = new SafeLoader(executorService, cache);

        getParametersDisplay();
        drawDisplayImage(imageView);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        registerGPSListeners(imageView);
        registerZoomListeners(imageView);
        registerTouchListeners(imageView);
    }

    private void getParametersDisplay() {
        final Display display = getWindowManager().getDefaultDisplay();
        final Point displaySize = new Point();
        display.getSize(displaySize);
        displayWidth = displaySize.x;
        displayHeight = displaySize.y;
    }

    private void registerZoomListeners(final ImageView imageView) {
        final ZoomControls zoom = (ZoomControls) findViewById(R.id.zoomControls1);

        final ZoomInClickListener zoomInClickListener = new ZoomInClickListener(imageView);
        final ZoomOutClickListener zoomOutClickListener = new ZoomOutClickListener(imageView);

        zoom.setOnZoomInClickListener(zoomInClickListener);
        zoom.setOnZoomOutClickListener(zoomOutClickListener);
    }

    private void registerBottonListener(final LocationManager locationManager, final ImageView imageView) {
        final Button button = (Button) findViewById(R.id.button);

        final ButtonOnClickListener buttonOnClickListener = new ButtonOnClickListener(locationManager,
            imageView, timeCorrection, this);
        button.setOnClickListener(buttonOnClickListener);
    }

    private void registerTouchListeners(final ImageView imageView) {
        final ViewOnTouchListener viewOnTouchListener = new ViewOnTouchListener(imageView, this);
        imageView.setOnTouchListener(viewOnTouchListener);
    }

    private void registerGPSListeners(final ImageView imageView) {
        locationManager = ((LocationManager) getSystemService(Context.LOCATION_SERVICE));
        if (!isGPSProviderEnabled()) {
            writeWarningMessage("GPS disabled!");
            LOG.warning("No GPS module finded.");
            return;
        }
        final Location cashLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if ((cashLocation != null) && (isActual(cashLocation, 0L))) {
            LOG.info("cashLocation is actual");
            sendInfoRequest(GeoPoints.makeGeoPoint(cashLocation), imageView);
        } else {
            LOG.info("cashLocation is not actual");
        }
        sendRequest(imageView);
    }

    private boolean isGPSProviderEnabled() {
        // TODO amosov-f: remove intent checking
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || getIntent().getBooleanExtra("enabled", false);
    }

    public void sendRequest(final ImageView imageView) {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new PositionUpdater(locationManager, imageView));
    }

    public boolean isActual(final Location location, final Long timeCorrection) {
        LOG.info("locaction time is " + location.getTime());
        return (location.getTime() + timeCorrection + FIVETEEN_MINUTES > (new Date()).getTime());
    }

    private void drawDisplayImage(final ImageView imageView) {
        originScheme = cache.loadCachedOrDefaultScheme();
        imageView.setImageBitmap(originScheme);
        LOG.info("Image XScale " + imageView.getScaleX());
        LOG.info("Display width " + displayWidth);
        LOG.info("Display high " + displayHeight);
    }

    // TODO: rewrite and take to another file/class
    private void sendInfoRequest(final GeoPoint geoPosition, final ImageView imageView) {
        final double lat = geoPosition.getLatitude();
        final double lon = geoPosition.getLongitude();
        LOG.info("GeoPoint coordinates " + "lat: " + lat + "lon: " + lon);
        final JSONObject root;
        try {
            root = loader.getJSON(lat, lon, this.infoURL);
        } catch (MalformedURLException ignored) {
            LOG.warning("Can't construct URL for info");
            writeWarningMessage("Internet disabled!");
            return;
        }

        if (root == null) {
            LOG.warning("Can't construct URL for info");
            return;
        }

        final InfoResponce responce = InfoResponceSerializer.deserialize(root);
        if (responce == null || responce.getClosestMarkets() == null || responce.getClosestMarkets().size() < 1) {
            originScheme = cache.loadCachedOrDefaultScheme();
            LOG.warning("No markets in responce");
        } else {
            final String schemaFullURL = this.schemaURL + responce.getClosestMarkets().get(0).getUrl();
            try {
                originScheme = loader.getScheme(schemaFullURL);
            } catch (MalformedURLException e) {
                LOG.warning("Can't construct url for scheme. " + e.getMessage());
                writeWarningMessage("Internet disabled!");
                return;
            }
        }

        imageView.setImageBitmap(originScheme);
        cache.saveSchemeToCache(originScheme);

        LOG.info("GeoPosition " + geoPosition);
        writeWarningMessage("GeoPosition " + geoPosition);
    }

    private void getProperties(@NotNull final String path) {
        try {
            final Config config = Config.load(path);
            nThread = config.getInt("app.request.pool.size");
            infoURL = config.getProperty("app.server.info.host");
            schemaURL = config.getProperty("app.server.schema.host");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void writeWarningMessage(@NotNull final String message) {
        Toast.makeText(AppActivity.this, message, Toast.LENGTH_LONG).show();
    }

    public void crashApplication() {
        throw new RuntimeException("I am crashed");
    }

    @Override
    public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        // for the system's orientation sensor registered listeners
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // to stop the listener and save battery
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(final SensorEvent event) {
        // get the angle around the z-axis rotated
        if (event.timestamp < timeStamp + THREE_SECONDS) {
            return;
        }
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
            LOG.info("timeStamp is  " + event.timestamp);
            //LOG.info("orientation: " + Math.toDegrees(orientation[0]) + " " + Math.toDegrees(orientation[1])
            //         + " " + Math.toDegrees(orientation[2]));
            timeStamp = event.timestamp;

            LOG.info("rotation parameters: " + currentDegree + " " + -azimuthInDegress);
            RotateAnimation ra;
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
            ra.setDuration(250);
            ra.setFillAfter(true);

            imageView.startAnimation(ra);
            currentDegree = -azimuthInDegress;
        }
    }

    public float getCurrentDegreeInRadian() {
        return (float) Math.toRadians(currentDegree);
    }

    @TestOnly
    @Nullable
    Location getLocation() {
        return location;
    }

    // TODO: take from here to another file
    private final class PositionUpdater implements LocationListener {
        @NotNull
        private final LocationManager manager;
        @NotNull
        private final ImageView myView;

        PositionUpdater(@NotNull final LocationManager manager, @NotNull final ImageView imageView) {
            this.manager = manager;
            myView = imageView;
        }

        @Override
        public void onLocationChanged(@NotNull final Location location) {
            AppActivity.this.location = location;

            if (timeCorrection == null) {
                timeCorrection = (new Date()).getTime() - location.getTime();
                LOG.warning("Time correction is " + timeCorrection);
                registerBottonListener(manager, myView);
            }
            LOG.info("onLocationChanged");

            manager.removeUpdates(this);

            final GeoPoint geoPosition = GeoPoints.makeGeoPoint(location);
            sendInfoRequest(geoPosition, myView);
        }

        @Override
        public void onStatusChanged(@NotNull final String provider, final int status, @NotNull final Bundle extras) {
        }

        @Override
        public void onProviderEnabled(@NotNull final String provider) {
        }

        @Override
        public void onProviderDisabled(@NotNull final String provider) {
        }
    }
}