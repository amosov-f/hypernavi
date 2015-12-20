package ru.hypernavi.client.marker;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
import ru.hypernavi.util.GeoPointImpl;

public final class MarkerActivity extends Activity {
    private static final Logger LOG = Logger.getLogger(MarkerActivity.class.getName());

    private static final String SCHEME_PATH = "/okey/okey_tallinskoe_shema_magazina_b.jpg";
    private static final int CIRCLE_RADIUS = 50;

    @Nullable
    private Point schemePosition;
    private boolean updatingGeoPosition;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);

        final Bitmap scheme = BitmapFactory.decodeStream(getClass().getResourceAsStream(SCHEME_PATH));

        final ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageBitmap(scheme);

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(@NotNull final View v, @NotNull final MotionEvent event) {
                if (updatingGeoPosition) {
                    return false;
                }

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
                return true;
            }
        });

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(@NotNull final View v) {
                LOG.fine("onClick");
                if (schemePosition == null) {
                    return;
                }
                final LocationManager locationManager = ((LocationManager) getSystemService(Context.LOCATION_SERVICE));
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Toast.makeText(MarkerActivity.this, "GPS disabled!", Toast.LENGTH_SHORT).show();
                    return;
                }

                updatingGeoPosition = true;

                findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                findViewById(R.id.button).setVisibility(View.INVISIBLE);

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new PositionUpdater(locationManager));
            }
        });
    }

    private final class PositionUpdater implements LocationListener {
        private static final int N_LOCATIONS = 5;

        @NotNull
        private final LocationManager manager;
        @NotNull
        private final List<Location> locations = new ArrayList<>();

        PositionUpdater(@NotNull final LocationManager manager) {
            this.manager = manager;
        }

        @Override
        public void onLocationChanged(@NotNull final Location location) {
            LOG.fine("onLocationChanged");
            locations.add(location);
            if (locations.size() != N_LOCATIONS) {
                return;
            }
            manager.removeUpdates(this);

            final GeoPointImpl geoPosition = average(locations);

            findViewById(R.id.button).setVisibility(View.VISIBLE);
            findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);

            updatingGeoPosition = false;

            LOG.info(schemePosition + " -> " + geoPosition);
            Toast.makeText(MarkerActivity.this, schemePosition + " -> " + geoPosition, Toast.LENGTH_SHORT).show();
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
        private GeoPointImpl average(@NotNull final List<Location> locations) {
            double sumLat = 0;
            double sumLon = 0;
            for (final Location location : locations) {
                sumLat += location.getLatitude();
                sumLon += location.getLongitude();
            }
            return new GeoPointImpl(sumLon / locations.size(), sumLat / locations.size());
        }
    }
}

