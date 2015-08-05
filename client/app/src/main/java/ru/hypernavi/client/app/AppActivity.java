package ru.hypernavi.client.app;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


import android.app.Activity;
import android.content.Context;
import android.graphics.*;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ZoomControls;
import ru.hypernavi.util.GeoPoint;

public final class AppActivity extends Activity {
    private static final Logger LOG = Logger.getLogger(AppActivity.class.getName());

    private static final String SCHEME_PATH = "/file_not_found.jpg";
    private static final int CIRCLE_RADIUS = 50;

    @Nullable
    private Point schemePosition;
    private boolean updatingGeoPosition;

    private Bitmap scheme;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        LOG.info("onCreate start");

        scheme = BitmapFactory.decodeStream(getClass().getResourceAsStream(SCHEME_PATH));

        final ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageBitmap(scheme);

        final ZoomControls zoom = (ZoomControls) findViewById(R.id.zoomControls1);

        zoom.setOnZoomInClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                final float maxScale = 10;
                final float step = 0.5f;

                final float x = imageView.getScaleX();
                final float y = imageView.getScaleY();

                if ((x > maxScale - step) || (y > maxScale - step)) {
                    imageView.setScaleX(maxScale);
                    imageView.setScaleY(maxScale);
                } else {
                    imageView.setScaleX(x + step);
                    imageView.setScaleY(y + step);
                }
            }
        });

        zoom.setOnZoomOutClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                final float minScale = 0.5f;
                final float step = 0.5f;

                final float x = imageView.getScaleX();
                final float y = imageView.getScaleY();

                if ((x < minScale + step) || (y < minScale + step)) {
                    imageView.setScaleX(minScale);
                    imageView.setScaleY(minScale);
                } else {
                    imageView.setScaleX(x - step);
                    imageView.setScaleY(y - step);
                }
            }
        });

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(@NotNull final View v, @NotNull final MotionEvent event) {
                if (updatingGeoPosition) {
                    return false;
                }
                LOG.info("Touch");

                final Matrix inverse = new Matrix();
                imageView.getImageMatrix().invert(inverse);
                final float[] schemePoint = {event.getX(), event.getY()};
                inverse.mapPoints(schemePoint);
                final int x = (int) schemePoint[0];
                final int y = (int) schemePoint[1];

                if (x < 0 || x >= scheme.getWidth() || y < 0 || y >= scheme.getHeight()) {
                    return false;
                }

                final Paint paint = new Paint();
                paint.setAntiAlias(true);
                paint.setColor(Color.RED);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(7);

                final Bitmap image = scheme.copy(Bitmap.Config.ARGB_8888, true);

                final Canvas canvas = new Canvas(image);
                canvas.drawCircle(x, y, CIRCLE_RADIUS, paint);
                canvas.drawPoint(x, y, paint);
                schemePosition = new Point(x, y);

                imageView.setImageBitmap(image);
                LOG.info("End touch");
                return true;
            }
        });

        final LocationManager locationManager = ((LocationManager) getSystemService(Context.LOCATION_SERVICE));
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(AppActivity.this, "GPS disabled!", Toast.LENGTH_SHORT).show();
            return;
        }

        updatingGeoPosition = true;

       locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
            new PositionUpdater(locationManager, imageView));
    }

    private final class PositionUpdater implements LocationListener {
        private static final int N_LOCATIONS = 1;

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

            scheme = extructScheme(lat, lon);

            myView.setImageBitmap(scheme);

            updatingGeoPosition = false;

            LOG.info(schemePosition + " -> " + geoPosition);
            Toast.makeText(AppActivity.this, schemePosition + " -> " + geoPosition, Toast.LENGTH_SHORT).show();

            final LocationManager locationManager = ((LocationManager) getSystemService(Context.LOCATION_SERVICE));
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(AppActivity.this, "GPS disabled!", Toast.LENGTH_SHORT).show();
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
                new PositionUpdater(locationManager, myView));
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

    private Bitmap extructScheme(final double lat, final double lon) {
        final Bitmap[] localScheme = {null};
        final Thread secondary = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final URLConnection myConnection = new URL("http://hypernavi.cloudapp.net/schema?lat="
                                                               + lat + "&lon=" + lon).openConnection();
                    localScheme[0] = BitmapFactory.decodeStream(myConnection.getInputStream());
                } catch (IOException e) {
                    LOG.warning(e.getMessage());
                    LOG.warning("Can't read from internet");
                }
            }
        });
        secondary.start();
        try {
            secondary.join();
        } catch (InterruptedException e) {
            LOG.warning(e.getMessage());
        }
        if (localScheme[0] == null) {
            localScheme[0] = BitmapFactory.decodeStream(getClass().getResourceAsStream(SCHEME_PATH));
        }
        return  localScheme[0];
    }
}

