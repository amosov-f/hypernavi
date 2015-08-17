package ru.hypernavi.client.app;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.logging.Logger;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Display;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ZoomControls;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import ru.hypernavi.commons.InfoResponce;
import ru.hypernavi.commons.InfoResponceSerializer;
import ru.hypernavi.util.GeoPoint;

public final class AppActivity extends Activity {
    private static final Logger LOG = Logger.getLogger(AppActivity.class.getName());

    private static final String SCHEME_PATH = "/file_not_found.jpg";
    private static final long MIN_TIME_BETWEEN_GPS_UPDATES = 5000;

    private Bitmap originScheme;
    @Nullable
    private JSONObject root;
    private int displayWidth;
    private int displayHeight;

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

    private void registerTouchListeners(final ImageView imageView) {
        final ViewOnTouchListener viewOnTouchListener = new ViewOnTouchListener(displayWidth, displayHeight, imageView);
        imageView.setOnTouchListener(viewOnTouchListener);
    }

    private void registerGPSListeners(final ImageView imageView) {
        final LocationManager locationManager = ((LocationManager) getSystemService(Context.LOCATION_SERVICE));
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(AppActivity.this, "GPS disabled!", Toast.LENGTH_SHORT).show();
            LOG.warning("No GPS module finded.");
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BETWEEN_GPS_UPDATES, 0,
                new PositionUpdater(locationManager, imageView));
    }


    private void drawDisplayImage(final ImageView imageView) {
        originScheme = loadDefaultScheme();
        imageView.setImageBitmap(originScheme);
        LOG.info("Image XScale " + imageView.getScaleX());
        LOG.info("Display width " + displayWidth);
        LOG.info("Display high " + displayHeight);
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LOG.info("onCreate start");

        setContentView(R.layout.main);
        final ImageView imageView = (ImageView) findViewById(R.id.imageView);

        getParametersDisplay();
        drawDisplayImage(imageView);

        registerZoomListeners(imageView);
        registerTouchListeners(imageView);
        registerGPSListeners(imageView);
    }

    private final class PositionUpdater implements LocationListener {
        private static final int N_LOCATIONS = 3;

        @NotNull
        private final LocationManager manager;
        @NotNull
        private final List<Location> locations = new ArrayList<>();
        @NotNull
        private final ImageView myView;

        PositionUpdater(@NotNull final LocationManager manager, @NotNull final ImageView imageView) {
            this.manager = manager;
            myView = imageView;
        }

        @Override
        public void onLocationChanged(@NotNull final Location location) {
            LOG.info("onLocationChanged");
            locations.add(location);
            if (locations.size() != N_LOCATIONS) {
                return;
            }
            manager.removeUpdates(this);

            final GeoPoint geoPosition = average(locations);

            final double lat = geoPosition.getLatitude();
            final double lon = geoPosition.getLongitude();

            extructJSON(lat, lon);

            final InfoResponce responce = InfoResponceSerializer.deserialize(root);
            if (responce == null || responce.getClosestMarkets() == null || responce.getClosestMarkets().size() < 1) {
                originScheme = loadDefaultScheme();
                LOG.warning("No markets in responce.");
            } else {
                final String schemaURL = "http://10.0.2.2:8080" + responce.getClosestMarkets().get(0).getUrl();
                extructScheme(schemaURL);
            }
            myView.setImageBitmap(originScheme);

            LOG.info("GeoPosition " + geoPosition);
            Toast.makeText(AppActivity.this, "GeoPosition " + geoPosition, Toast.LENGTH_SHORT).show();

            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BETWEEN_GPS_UPDATES, 0, new PositionUpdater(manager, myView));
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

        @NotNull
        private GeoPoint average(@NotNull final List<Location> locations) {
            double sumLat = 0;
            double sumLon = 0;
            for (final Location location : locations) {
                sumLat += location.getLatitude();
                sumLon += location.getLongitude();
            }
            return new GeoPoint(sumLat / locations.size(), sumLon / locations.size());
        }
    }

    // TODO: move to module util
    @NotNull
    private Properties loadProperties(@NotNull final String resourcesPath) {
        final Properties properties = new Properties();
        try {
            properties.load(AppActivity.class.getResourceAsStream(resourcesPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties;
    }

    // TODO: move extructor to another module
    private void extructJSON(final double lat, final double lon) {
        String hypermarketsJSON;
        final ExecutorService service = Executors.newFixedThreadPool(1);
        final Future<String> task = service.submit(new Callable<String>() {
            @Nullable
            @Override
            public String call() throws Exception {
                try {
                    final URLConnection myConnection = new URL("http://10.0.2.2:8080/schemainfo?lat="
                            + lat + "&lon=" + lon).openConnection();
                    return IOUtils.toString(myConnection.getInputStream());
                } catch (IOException e) {
                    LOG.warning(e.getMessage());
                    LOG.warning("Can't read string from internet");
                    return null;
                }
            }
        });
        try {
            hypermarketsJSON = task.get();
        } catch (InterruptedException e) {
            LOG.warning(e.getMessage());
            hypermarketsJSON = null;
        } catch (ExecutionException e) {
            LOG.warning("Oops.");
            throw new RuntimeException(e);
        }
        if (hypermarketsJSON == null) {
            Toast.makeText(AppActivity.this, "JSON string is null", Toast.LENGTH_SHORT).show();
            LOG.warning("JSON string is null");
            return;
        }
        try {
            root = new JSONObject(hypermarketsJSON);
            LOG.info(root.toString());
        } catch (JSONException e) {
            LOG.warning(e.getMessage());
            root = null;
        }
    }

    @NotNull
    private Bitmap loadDefaultScheme() {
        final Bitmap defaultScheme = BitmapFactory.decodeStream(getClass().getResourceAsStream(SCHEME_PATH));
        if (defaultScheme == null) {
            throw new RuntimeException("No default scheme founded");
        }
        return defaultScheme;
    }

    private void extructScheme(final String currentSchemeURL) {
        final ExecutorService service = Executors.newFixedThreadPool(1);
        final Future<Bitmap> task = service.submit(new Callable<Bitmap>() {
            @Nullable
            @Override
            public Bitmap call() throws Exception {
                try {
                    final URLConnection myConnection = new URL(currentSchemeURL).openConnection();
                    return BitmapFactory.decodeStream(myConnection.getInputStream());
                } catch (IOException e) {
                    LOG.warning(e.getMessage());
                    LOG.warning("Can't read from internet");
                    return null;
                }

            }
        });

        try {
            originScheme = task.get();
            if (originScheme == null) {
                originScheme = loadDefaultScheme();
            }
        } catch (InterruptedException e) {
            LOG.warning(e.getMessage());
            originScheme = loadDefaultScheme();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}