package ru.hypernavi.client.app;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.*;
import java.util.logging.Logger;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ZoomControls;
import org.json.JSONException;
import org.json.JSONObject;
import ru.hypernavi.client.app.util.GeoPoints;
import ru.hypernavi.commons.InfoResponce;
import ru.hypernavi.commons.InfoResponceSerializer;
import ru.hypernavi.util.Config;
import ru.hypernavi.util.GeoPoint;

public final class AppActivity extends Activity implements SensorEventListener {
    private static final Logger LOG = Logger.getLogger(AppActivity.class.getName());
    private static final String LOCAL_FILE_NAME = "cacheScheme.png";
    private static final String SCHEME_PATH = "/file_not_found.jpg";
    private static final String PROPERTIES_SCHEME = "classpath:/app-common.properties";
    private static final long MAX_TIME_OUT = 5000L;
    private static final long THREE_SECONDS = 3000000000L;
    //noinspection MagicNumber
    private static final int FIVETEEN_MINUTES = 1000 * 50 * 1;

    private Bitmap originScheme;
    @Nullable
    private JSONObject root;
    private int displayWidth;
    private int displayHeight;

    private ImageView imageView;

    private int nThread;
    private ExecutorService executorService;
    private String infoURL;
    private String schemaURL;

    private LocationManager locationManager;
    private Long timeCorrection;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private boolean lastAccelerometerSet = false;
    private boolean lastMagnetometerSet = false;
    private float[] lastAccelerometer = new float[3];
    private float[] lastMagnetometer = new float[3];
    private long timeStamp = (new Date()).getTime();

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LOG.info("onCreate start");

        setContentView(R.layout.main);

        try {
            executeProperties(Config.load(PROPERTIES_SCHEME));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        executorService = Executors.newFixedThreadPool(nThread);

        imageView = (ImageView) findViewById(R.id.imageView);

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
        final ViewOnTouchListener viewOnTouchListener = new ViewOnTouchListener(displayWidth, displayHeight, imageView);
        imageView.setOnTouchListener(viewOnTouchListener);
    }

    public void sendRequest(final ImageView imageView) {
        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new PositionUpdater(locationManager, imageView), null);
    }

    private void registerGPSListeners(final ImageView imageView) {
        locationManager = ((LocationManager) getSystemService(Context.LOCATION_SERVICE));
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(AppActivity.this, "GPS disabled!", Toast.LENGTH_LONG).show();
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

    public boolean isActual(final Location location, final Long timeCorrection) {
        LOG.info("locaction time is " + location.getTime());
        return (location.getTime() + timeCorrection + FIVETEEN_MINUTES > (new Date()).getTime());
    }

    private void drawDisplayImage(final ImageView imageView) {
        originScheme = loadCachedOrDefaultScheme();
        imageView.setImageBitmap(originScheme);
        LOG.info("Image XScale " + imageView.getScaleX());
        LOG.info("Display width " + displayWidth);
        LOG.info("Display high " + displayHeight);
    }

    private void sendInfoRequest(final GeoPoint geoPosition, final ImageView imageView) {
        final double lat = geoPosition.getLatitude();
        final double lon = geoPosition.getLongitude();
        LOG.info("GeoPoint coordinates " + "lat: " + lat + "lon: " + lon);
        try {
            //final String infoURL = "http://10.0.2.2:8080/schemainfo";
            root = extructJSON(lat, lon, this.infoURL);
            LOG.info("infoURL: " + this.infoURL);
        } catch (MalformedURLException e) {
            LOG.warning("can't construct URL for info");
            Toast.makeText(AppActivity.this, "Internet disabled!", Toast.LENGTH_LONG).show();
            return;
            //throw new RuntimeException(e);
        }

        if (root == null) {
            LOG.warning("can't construct URL for info");
            return;
        }

        final InfoResponce responce = InfoResponceSerializer.deserialize(root);
        if (responce == null || responce.getClosestMarkets() == null || responce.getClosestMarkets().size() < 1) {
            originScheme = loadCachedOrDefaultScheme();
            LOG.warning("No markets in responce.");
        } else {
            //final String schemaURL = "http://10.0.2.2:8080" + responce.getClosestMarkets().get(0).getUrl();
            final String schemaFullURL = this.schemaURL + responce.getClosestMarkets().get(0).getUrl();
            try {

                originScheme = extructScheme(schemaFullURL);
            } catch (MalformedURLException e) {
                LOG.warning("Can't construct url for scheme. " + e.getMessage());
                Toast.makeText(AppActivity.this, "Internet disabled!", Toast.LENGTH_LONG).show();
                return;
            }
        }

        imageView.setImageBitmap(originScheme);
        cachedScheme();

        LOG.info("GeoPosition " + geoPosition);
        Toast.makeText(AppActivity.this, "GeoPosition " + geoPosition, Toast.LENGTH_SHORT).show();
    }
    //
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
            LOG.info("orientation: " + Math.toDegrees(orientation[0]) + " " + Math.toDegrees(orientation[1])
                     + " " + Math.toDegrees(orientation[2]));
            timeStamp = event.timestamp;

            imageView.setRotation(-azimuthInDegress);
            LOG.info("imageView rotation around pivot " + imageView.getRotation());
        }
    }
    //
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

    private void executeProperties(@NotNull final Config config) {
        nThread = config.getInt("app.request.pool.size");
        infoURL = config.getProperty("app.server.info.host");
        schemaURL = config.getProperty("app.server.schema.host");
    }

    // TODO: move extructor to another module
    private JSONObject extructJSON(final double lat, final double lon, final String infoURL) throws MalformedURLException {
        final URL requestURL = new URL(infoURL + "?lat=" + lat + "&lon=" + lon);
        final RequestString requestString = new RequestString(requestURL);
        LOG.info("JSON request URL" + requestURL.toString());
        final Future<String> task = executorService.submit(requestString);
        String hypermarketsJSON;
        try {
            hypermarketsJSON = task.get(MAX_TIME_OUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            LOG.warning(e.getMessage());
            hypermarketsJSON = null;
        } catch (ExecutionException e) {
            LOG.warning("Oops.");
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
        if (hypermarketsJSON == null) {
            LOG.warning("JSON string is null");
            return null;
        }
        try {
            return new JSONObject(hypermarketsJSON);
        } catch (JSONException e) {
            LOG.warning(e.getMessage());
            return null;
        }
    }

    private Bitmap extructScheme(final String currentSchemeURL) throws MalformedURLException {
        final RequestBitmap requestBitmap = new RequestBitmap(new URL(currentSchemeURL));
        final Future<Bitmap> task = executorService.submit(requestBitmap);

        try {
            return task.get(MAX_TIME_OUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            LOG.warning(e.getMessage());
            return loadCachedOrDefaultScheme();
        } catch (ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private Bitmap loadCachedOrDefaultScheme() {
        final File file = new File(this.getFilesDir(), LOCAL_FILE_NAME);
        if (file.exists()) {
            try {
                final Bitmap cachedScheme = BitmapFactory.decodeStream(new FileInputStream(file));
                if (cachedScheme == null) {
                    LOG.warning("File: " + file.getPath());
                    LOG.warning("No cached scheme founded in existing file " + file.getPath());
                    this.deleteFile(file.getPath());
                } else {
                    LOG.info("cached scheme is returned");
                    return cachedScheme;
                }
            } catch (FileNotFoundException e) {
                LOG.warning("can't find file " + e.getMessage());
            }
        }

        final Bitmap defaultScheme = BitmapFactory.decodeStream(getClass().getResourceAsStream(SCHEME_PATH));
        if (defaultScheme == null) {
            throw new RuntimeException("No default scheme founded");
        }
        LOG.info("default scheme is returned");
        return defaultScheme;
    }

    private void cachedScheme() {
        final File file = new File(this.getFilesDir(), LOCAL_FILE_NAME);
        try {
            if (file.createNewFile()) {
                try {
                    final FileOutputStream out = new FileOutputStream(file);
                    LOG.warning("file where we write: " + file.toString());
                    originScheme.compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.close();
                    LOG.info("scheme is cached");
                } catch (FileNotFoundException e) {
                    LOG.warning("cached file is null " + e.getMessage());
                } catch (IOException e) {
                    LOG.warning("can't close outputStream " + e.getMessage());
                }
            }
        } catch (IOException e) {
            LOG.warning("can't create file for cached scheme " + e.getMessage());
        }
    }

    public void crashApplication() {
        throw new RuntimeException("I am crashed");
    }
}