package ru.hypernavi.client.app;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.logging.Logger;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.*;
import ru.hypernavi.client.app.listeners.*;
import ru.hypernavi.client.app.util.CacheWorker;
import ru.hypernavi.client.app.util.GeoPointsUtils;
import ru.hypernavi.client.app.util.InfoRequestHandler;
import ru.hypernavi.commons.Hypermarket;
import ru.hypernavi.commons.InfoResponse;
import ru.hypernavi.util.GeoPoint;

public final class AppActivity extends Activity {
    private static final Logger LOG = Logger.getLogger(AppActivity.class.getName());
    public static final String PROPERTIES_SCHEME = "classpath:/app-common.properties";

    private InfoResponse infoResponse;
    private Bitmap originScheme;

    private ImageView imageView;

    private LocationManager locationManager;
    private PositionUpdater positionUpdater;

    private OrientationEventListener orientationEventListener;

    private InfoRequestHandler handler;
    private CacheWorker cache;

    ToggleButton toogleButton;
    private float currentMapAzimuthInDegrees;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LOG.info("onCreate start");

        setContentView(R.layout.main);
        imageView = (ImageView) findViewById(R.id.imageView);

        cache = new CacheWorker(this);
        handler = new InfoRequestHandler(this, cache);

        drawDisplayImage(imageView);

        currentMapAzimuthInDegrees = 0;

        orientationEventListener = new OrientationEventListener(imageView, this);
        orientationEventListener.onStandby();

        final CheckedChangeListener checkedChangeListener = new CheckedChangeListener(this, orientationEventListener);

        toogleButton = (ToggleButton) findViewById(R.id.toggleButton1);

        toogleButton.setOnCheckedChangeListener(checkedChangeListener);

        registerGPSListeners(imageView);
        registerZoomListeners(imageView);
        registerTouchListeners(imageView);

        LOG.info("onCreate finished");
    }

    private void registerZoomListeners(final ImageView imageView) {
        final ZoomButton zoomPlus = (ZoomButton) findViewById(R.id.zoomButton1);
        final ZoomButton zoomMinus = (ZoomButton) findViewById(R.id.zoomButton);
        final ZoomClickListener zoomInClickListener = new ZoomClickListener(imageView, true);
        final ZoomClickListener zoomOutClickListener = new ZoomClickListener(imageView, false);

        zoomPlus.setOnClickListener(zoomInClickListener);
        zoomMinus.setOnClickListener(zoomOutClickListener);
    }

    private void registerTouchListeners(final ImageView imageView) {
        final ViewOnTouchListener viewOnTouchListener = new ViewOnTouchListener(imageView, orientationEventListener);
        imageView.setOnTouchListener(viewOnTouchListener);
    }

    private void registerGPSListeners(final ImageView imageView) {
        locationManager = ((LocationManager) getSystemService(Context.LOCATION_SERVICE));
        positionUpdater = new PositionUpdater(locationManager, imageView, (Button) findViewById(R.id.button), this);
        if (!isGPSProviderEnabled()) {
            writeWarningMessage("Включите определение местоположения по GPS");
            LOG.warning("No GPS module finded.");
            return;
        }
        final Location cashLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if ((cashLocation != null) && (PositionUpdater.isActual(cashLocation, 0L))) {
            LOG.info("cashLocation is actual");
            processInfo(GeoPointsUtils.makeGeoPoint(cashLocation), imageView);
        } else {
            LOG.info("cashLocation is not actual");
        }
        sendRequest();
    }

    private boolean isGPSProviderEnabled() {
        // TODO amosov-f: remove intent checking
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || getIntent().getBooleanExtra("enabled", false);
    }

    public void sendRequest() {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, positionUpdater);
        LOG.info("request to update location is sent");
    }

    private void drawDisplayImage(final ImageView imageView) {
        originScheme = cache.loadCachedOrDefaultScheme();
        imageView.setImageBitmap(originScheme);
    }

    public void processInfo(final GeoPoint geoPosition, final ImageView imageView) {

        infoResponse = handler.getInfoResponse(geoPosition);
        if (infoResponse != null) {
            LOG.warning("infoResponse is null !!!");
            final ArrayList<Hypermarket> hypermarkets = (ArrayList<Hypermarket>) infoResponse.getClosestMarkets();
            if (hypermarkets == null) {
                currentMapAzimuthInDegrees = 0;
            } else {
                currentMapAzimuthInDegrees = (float) infoResponse.getClosestMarkets().get(0).getOrientation();
            }
        }
        originScheme = handler.getClosestSchema(infoResponse);

        LOG.info("schema and azimuth is loaded");

        if (originScheme == null) {
            originScheme = cache.loadCachedOrDefaultScheme();
            LOG.warning("Problems with scheme above");
        }

        imageView.setImageBitmap(originScheme);
        cache.saveSchemeToCache(originScheme);

        LOG.info("GeoPosition " + geoPosition);
        writeWarningMessage("GeoPosition " + geoPosition);
    }

    public void writeWarningMessage(@NotNull final String message) {
        Toast.makeText(AppActivity.this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (toogleButton.isChecked()) {
            // for the system's orientation sensor registered listeners
            orientationEventListener.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // to stop the sensor listener and save battery
        orientationEventListener.onPause();
    }

    public float getCurrentMarketAzimuthInDegrees() {
        return currentMapAzimuthInDegrees;
    }
}