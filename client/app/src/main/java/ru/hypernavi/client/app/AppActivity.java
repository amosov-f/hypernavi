package ru.hypernavi.client.app;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Display;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ZoomControls;
import ru.hypernavi.client.app.util.CacheWorker;
import ru.hypernavi.client.app.util.GeoPointsUtils;
import ru.hypernavi.client.app.util.InfoRequestHandler;
import ru.hypernavi.client.app.util.SafeLoader;
import ru.hypernavi.util.Config;
import ru.hypernavi.util.GeoPoint;

public final class AppActivity extends Activity {
    private static final Logger LOG = Logger.getLogger(AppActivity.class.getName());
    public static final String PROPERTIES_SCHEME = "classpath:/app-common.properties";
    // TODO amosov-f: WTF!
    private static final int FIVETEEN_MINUTES = 15 * 1000 * 60;

    private Bitmap originScheme;
    private int displayWidth;
    private int displayHeight;

    private ImageView imageView;

    private int nThread;
    private ExecutorService executorService;

    private LocationManager locationManager;
    private Long timeCorrection;

    private OrientationEventListener orientationEventListener;

    private InfoRequestHandler handler;
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
        handler = new InfoRequestHandler(this, loader, cache);

        getParametersDisplay();
        drawDisplayImage(imageView);

        orientationEventListener = new OrientationEventListener(imageView, this);

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
        final ViewOnTouchListener viewOnTouchListener = new ViewOnTouchListener(imageView, orientationEventListener);
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
            processInfo(GeoPointsUtils.makeGeoPoint(cashLocation), imageView);
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
    private void processInfo(final GeoPoint geoPosition, final ImageView imageView) {
        originScheme = handler.getSchemaResponce(geoPosition);

        if (originScheme == null) {
            originScheme = cache.loadCachedOrDefaultScheme();
            LOG.warning("Problems with scheme above");
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void writeWarningMessage(@NotNull final String message) {
        Toast.makeText(AppActivity.this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // for the system's orientation sensor registered listeners
        orientationEventListener.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // to stop the sensor listener and save battery
        orientationEventListener.onPause();
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

            final GeoPoint geoPosition = GeoPointsUtils.makeGeoPoint(location);
            processInfo(geoPosition, myView);
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