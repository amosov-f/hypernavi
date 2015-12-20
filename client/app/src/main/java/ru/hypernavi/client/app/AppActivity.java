package ru.hypernavi.client.app;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.logging.Logger;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import ru.hypernavi.client.app.listeners.*;
import ru.hypernavi.client.app.util.CacheWorker;
import ru.hypernavi.client.app.util.GeoPointsUtils;
import ru.hypernavi.client.app.util.InfoRequestHandler;
import ru.hypernavi.client.app.util.LogoLoader;
import ru.hypernavi.commons.Hypermarket;
import ru.hypernavi.commons.InfoResponse;
import ru.hypernavi.util.GeoPoint;
import ru.hypernavi.util.GeoPointImpl;


public final class AppActivity extends Activity {
    private static final Logger LOG = Logger.getLogger(AppActivity.class.getName());
    public static final String PROPERTIES_SCHEME = "classpath:/app-common.properties";
    public static final String ZOOM_IN_PATH = "/zoom_in.png";
    public static final String ZOOM_OUT_PATH = "/zoom_out.png";
    public static final String YANDEX_PATH = "/yandex_button.png";

    private InfoResponse infoResponse;

    private Bitmap originScheme;
    private float currentMapAzimuthInDegrees;
    private boolean hasOrientation;

    private ImageView marketImageView;
    private ImageView logoImageView;
    private TextView distanceText;
    private TextView adressText;
    private ImageView yandexButton;

    private LocationManager locationManager;
    private PositionUpdater positionUpdater;

    private ButtonOnClickListener buttonOnClickListener;

    private OrientationEventListener orientationEventListener;
    private CompasOnClickListener compasOnClickListener;

    private AdressListener adressListener;

    private InfoRequestHandler handler;
    private CacheWorker cache;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LOG.info("onCreate start");

        setContentView(R.layout.main);
        marketImageView = (ImageView) findViewById(R.id.imageView);
        logoImageView = (ImageView) findViewById(R.id.logoView);
        yandexButton = (ImageView) findViewById(R.id.yandexButton);
        distanceText = (TextView) findViewById(R.id.distanceView);
        distanceText.setVisibility(View.INVISIBLE);
        adressText = (TextView) findViewById(R.id.adressView);
        adressText.setVisibility(View.INVISIBLE);

        cache = new CacheWorker(this);
        handler = new InfoRequestHandler(this, cache);

        drawDisplayImage(true);

        currentMapAzimuthInDegrees = 0;
        hasOrientation = false;

        orientationEventListener = new OrientationEventListener(marketImageView, this);
        orientationEventListener.onStandby();

        final ImageView compasButton = (ImageView) findViewById(R.id.compasButton);
        compasOnClickListener = new CompasOnClickListener(this, orientationEventListener, compasButton);
        compasButton.setOnClickListener(compasOnClickListener);

        registerGPSListeners();
        registerZoomListeners();

        registerTouchListeners();
        registerAdressListeners();
        //
        LOG.info("onCreate finished");
    }

    private void registerButtonListener() {
        buttonOnClickListener = new ButtonOnClickListener(locationManager, this);
    }

    private void registerZoomListeners() {
        final ImageView zoomIn = (ImageView) findViewById(R.id.zoomInButton);
        zoomIn.setImageBitmap(BitmapFactory.decodeStream(getClass().getResourceAsStream(ZOOM_IN_PATH)));
        final ImageView zoomOut = (ImageView) findViewById(R.id.zoomOutButton);
        zoomOut.setImageBitmap(BitmapFactory.decodeStream(getClass().getResourceAsStream(ZOOM_OUT_PATH)));
        final ZoomClickListener zoomInClickListener = new ZoomClickListener(marketImageView, true, this);
        final ZoomClickListener zoomOutClickListener = new ZoomClickListener(marketImageView, false, this);

        zoomIn.setOnClickListener(zoomInClickListener);
        zoomOut.setOnClickListener(zoomOutClickListener);
    }

    private void registerTouchListeners() {
        marketImageView.setOnTouchListener(new ViewOnTouchListener(marketImageView, orientationEventListener, this));
    }

    private void registerGPSListeners() {
        locationManager = ((LocationManager) getSystemService(Context.LOCATION_SERVICE));
        registerButtonListener();
        positionUpdater = new PositionUpdater(locationManager, marketImageView, buttonOnClickListener, this);
        if (!isGPSProviderEnabled()) {
            writeWarningMessage("Включите определение местоположения по GPS");
            LOG.warning("No GPS module finded.");
            return;
        }
        Location cashLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (cashLocation == null) {
            cashLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        if ((cashLocation != null) && (PositionUpdater.isActual(cashLocation, 0L))) {
            LOG.info("cashLocation is actual");
            processInfo(GeoPointsUtils.makeGeoPoint(cashLocation), marketImageView);
        } else {
            LOG.info("cashLocation is not actual");
        }
        sendLocationRequest();
    }

    private boolean isGPSProviderEnabled() {
        // TODO amosov-f: remove intent checking
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
               locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ||
               getIntent().getBooleanExtra("enabled", false);
    }

    public void sendLocationRequest() {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, positionUpdater);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, positionUpdater);
        LOG.info("request to update location is sent");
    }

    private void registerAdressListeners() {
        adressListener = new AdressListener(this);
        yandexButton.setImageBitmap(BitmapFactory.decodeStream(getClass().getResourceAsStream(YANDEX_PATH)));
        yandexButton.setOnClickListener(adressListener);
    }

    private void drawDisplayImage(final boolean takeItFromCache) {
        if (takeItFromCache) {
            originScheme = cache.loadCachedOrDefaultScheme();
        }
        marketImageView.setImageBitmap(originScheme);
    }

    public void processInfo(final GeoPointImpl geoPosition, @NotNull final ImageView marketImageView) {
        final Bitmap oldScheme = originScheme.copy(originScheme.getConfig(), true);
        infoResponse = handler.getInfoResponse(geoPosition);
        if (infoResponse != null) {
            final ArrayList<Hypermarket> hypermarkets = (ArrayList<Hypermarket>) infoResponse.getClosestMarkets();
            if (hypermarkets != null) {
                cache.saveInfoResponseToCache(infoResponse);
                final GeoPoint mylocation = infoResponse.getLocation();
                final Hypermarket closestMarket = hypermarkets.get(0);
                final GeoPoint marketLocation = closestMarket.getLocation();
                distanceText.setText(new DecimalFormat("#.#").format(GeoPointImpl.distance(mylocation, marketLocation)) + " км");
                distanceText.setVisibility(distanceText.VISIBLE);
                if (closestMarket.getAddress().equals(closestMarket.getLine())) {
                    adressText.setText(closestMarket.getAddress());
                } else {
                    adressText.setText(closestMarket.getLine() + ", " + closestMarket.getNumber());
                }
                adressText.setVisibility(View.VISIBLE);
                hasOrientation = closestMarket.hasOrientation();
                LOG.info("is oriented? " + hasOrientation);
                if (hasOrientation) {
                    currentMapAzimuthInDegrees = (float) infoResponse.getClosestMarkets().get(0).getOrientation();
                    LOG.info("azimuth is loaded");
                }

                originScheme = handler.getClosestSchema(infoResponse);
                if (!oldScheme.sameAs(originScheme)) {
                    moveImageToStartPoint();
                    marketImageView.setImageBitmap(originScheme);
                    cache.saveSchemeToCache(originScheme);
                }
                LOG.info("schema is loaded");

                (new LogoLoader()).loadLogo(closestMarket.getType(), logoImageView);
            } else {
                LOG.warning("infoResponse is not null, but hypermarkets is null");
                writeWarningMessage("Can't load info from Internet");
            }
        }

        originScheme = handler.getClosestSchema(infoResponse);
    }

    public void writeWarningMessage(@NotNull final String message) {
        Toast.makeText(AppActivity.this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (compasOnClickListener.getClicked()) {
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

    public GeoPointImpl getClosestMarketLocation() {
        if (infoResponse == null) {
            return null;
        }
        final ArrayList<Hypermarket> hypermarkets = (ArrayList<Hypermarket>) infoResponse.getClosestMarkets();
        if(hypermarkets == null) {
            return null;
        } else {
            return hypermarkets.get(0).getLocation();
        }
    }

    public float getCurrentMarketAzimuthInDegrees() {
        return currentMapAzimuthInDegrees;
    }

    public boolean getHasOrientation() {
        return hasOrientation;
    }

    public boolean haveInfoResponse() {
        return infoResponse != null;
    }

    public void moveImageToStartPoint() {
        compasOnClickListener.moveImageToStratPoint();
        marketImageView.scrollTo(0, 0);
        marketImageView.setScaleX(1);
        marketImageView.setScaleY(1);
        LOG.info("image have moved to start point");
    }

    public void setOffPointButton() {
        buttonOnClickListener.setOffPointButton();
    }
}