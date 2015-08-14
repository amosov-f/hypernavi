package ru.hypernavi.client.app;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.*;
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
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ZoomControls;
import org.json.JSONException;
import org.json.JSONObject;
import ru.hypernavi.util.GeoPoint;
import org.apache.commons.io.IOUtils;

public final class AppActivity extends Activity {
    private static final Logger LOG = Logger.getLogger(AppActivity.class.getName());

    private static final String SCHEME_PATH = "/file_not_found.jpg";
    private static final int CIRCLE_RADIUS = 50;
    private static final long MIN_TIME_BETWEEN_GPS_UPDATES = 5000;

    @Nullable
    private Point schemePosition;

    private Bitmap originScheme;
    private JSONObject root;
    private int displayWidth;
    private int displayHigh;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final Display display = getWindowManager().getDefaultDisplay();
        final Point displaySize = new Point();
        display.getSize(displaySize);
        displayWidth = displaySize.x;
        displayHigh = displaySize.y;

        LOG.info("onCreate start");

        originScheme = BitmapFactory.decodeStream(getClass().getResourceAsStream(SCHEME_PATH));

        final ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageBitmap(originScheme);

        LOG.info( "Image XScale " + imageView.getScaleX());
        LOG.info("Display width " + displayWidth);
        LOG.info("Display high " + displayHigh);

        final ZoomControls zoom = (ZoomControls) findViewById(R.id.zoomControls1);

        zoom.setOnZoomInClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                final float maxScale = 4;
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

                final Display display = getWindowManager().getDefaultDisplay();
                final Point displaySize = new Point();
                display.getSize(displaySize);
                displayWidth = displaySize.x;
                displayHigh = displaySize.y;

                LOG.warning("Image XScale " + imageView.getScaleX());
                LOG.warning("Display width " + displayWidth);
                LOG.warning("Display high " + displayHigh);
            }
        });

        zoom.setOnZoomOutClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                final float minScale = 1.0f;
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

                final Display display = getWindowManager().getDefaultDisplay();
                final Point displaySize = new Point();
                display.getSize(displaySize);
                displayWidth = displaySize.x;
                displayHigh = displaySize.y;

                LOG.warning("Image XScale " + imageView.getScaleX());
                LOG.warning("Display width " + displayWidth);
                LOG.warning("Display high " + displayHigh);
            }
        });

        imageView.setOnTouchListener(new View.OnTouchListener() {
            float downX;
            float downY;
            int totalX;
            int totalY;
            int scrollByX;
            int scrollByY;
            @Override
            public boolean onTouch(@NotNull final View v, @NotNull final MotionEvent event) {
                LOG.info("Touch");

                final int maxLeft = -getMaxXScroll();
                final int maxRight = -maxLeft;
                final int maxTop = -getMaxYScroll();
                final int maxBottom = -maxTop;
                LOG.warning("maxLeft: " + maxLeft + " maxTop: " + maxTop );
                final float currentX;
                final float currentY;
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        downX = event.getX();
                        downY = event.getY();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        currentX = event.getX();
                        currentY = event.getY();
                        scrollByX = (int)(downX - currentX);
                        scrollByY = (int)(downY - currentY);

                        // scrolling to left side of image (pic moving to the right)
                        if (currentX > downX) {
                            if (totalX == maxLeft) {
                                scrollByX = 0;
                            }
                            if (totalX > maxLeft) {
                                totalX = totalX + scrollByX;
                            }
                            if (totalX < maxLeft) {
                                scrollByX = maxLeft - (totalX - scrollByX);
                                totalX = maxLeft;
                            }
                        }

                        // scrolling to right side of image (pic moving to the left)
                        if (currentX < downX) {
                            if (totalX == maxRight) {
                                scrollByX = 0;
                            }
                            if (totalX < maxRight) {
                                totalX = totalX + scrollByX;
                            }
                            if (totalX > maxRight) {
                                scrollByX = maxRight - (totalX - scrollByX);
                                totalX = maxRight;
                            }
                        }

                        // scrolling to top of image (pic moving to the bottom)
                        if (currentY > downY) {
                            if (totalY == maxTop) {
                                scrollByY = 0;
                            }
                            if (totalY > maxTop) {
                                totalY = totalY + scrollByY;
                            }
                            if (totalY < maxTop) {
                                scrollByY = maxTop - (totalY - scrollByY);
                                totalY = maxTop;
                            }
                        }

                        // scrolling to bottom of image (pic moving to the top)
                        if (currentY < downY) {
                            if (totalY == maxBottom) {
                                scrollByY = 0;
                            }
                            if (totalY < maxBottom) {
                                totalY = totalY + scrollByY;
                            }
                            if (totalY > maxBottom) {
                                scrollByY = maxBottom - (totalY - scrollByY);
                                totalY = maxBottom;
                            }
                        }

                        imageView.scrollBy(scrollByX, scrollByY);
                        downX = currentX;
                        downY = currentY;
                        break;
                    default:
                        LOG.warning("other event");
                }

                LOG.info("Touch ");

                final Matrix inverse = new Matrix();
                imageView.getImageMatrix().invert(inverse);
                final float[] schemePoint = {event.getX(), event.getY()};
                inverse.mapPoints(schemePoint);
                final int x = (int) schemePoint[0];
                final int y = (int) schemePoint[1];

                if (x < 0 || x >= originScheme.getWidth() || y < 0 || y >= originScheme.getHeight()) {
                    return false;
                }

                final Paint paint = new Paint();
                paint.setAntiAlias(true);
                paint.setColor(Color.RED);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(7);

                final Bitmap image = originScheme.copy(Bitmap.Config.ARGB_8888, true);
                LOG.warning("XTouch " + x);
                LOG.warning("YTouch " + y);

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

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BETWEEN_GPS_UPDATES, 0,
            new PositionUpdater(locationManager, imageView));

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

            try {
                // for .net server
                //final String schemURL = root.getJSONArray("hypermarkets").getJSONObject(0).getString("URL");

                // for localhost
                final double longitude = root.getJSONArray("hypermarkets").getJSONObject(0).getDouble("longitude");
                final double latitude = root.getJSONArray("hypermarkets").getJSONObject(0).getDouble("latitude");
                final String schemURL = "http://10.0.2.2:8080/schema?lon="+longitude+"&lat="+latitude;

                extructScheme(schemURL);
            } catch (JSONException e) {
                Toast.makeText(AppActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            myView.setImageBitmap(originScheme);

            LOG.info(schemePosition + " -> " + geoPosition);
            Toast.makeText(AppActivity.this, schemePosition + " -> " + geoPosition, Toast.LENGTH_SHORT).show();

            final LocationManager locationManager = ((LocationManager) getSystemService(Context.LOCATION_SERVICE));
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(AppActivity.this, "GPS disabled!", Toast.LENGTH_SHORT).show();
                return;
            }

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BETWEEN_GPS_UPDATES, 0,
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

    private void extructJSON(final double lat, final double lon) {
        final String[] jString = new String[1];
        final Thread secondary = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //final URLConnection myConnection = new URL("http://hypernavi.cloudapp.net/schemainfo?lat="
                    //                                           + lat + "&lon=" + lon).openConnection();
                    final URLConnection myConnection = new URL("http://10.0.2.2:8080/schemainfo?lat="
                                                               + lat + "&lon=" + lon).openConnection();
                    jString[0] = IOUtils.toString(myConnection.getInputStream());
                } catch (IOException e) {
                    LOG.warning(e.getMessage());
                    LOG.warning("Can't read string from internet");
                }
            }
        });
        secondary.start();
        try {
            secondary.join();
        } catch (InterruptedException e) {
            LOG.warning(e.getMessage());
        }
        if (jString[0] == null) {
            Toast.makeText(AppActivity.this, "JSON string is null", Toast.LENGTH_SHORT).show();
            LOG.warning("JSON string is null");
        } else {
            try {
                root = new JSONObject(jString[0]);
                LOG.info(root.toString());
            } catch (JSONException e) {
                LOG.warning(e.getMessage());
            }
        }
    }

    private void extructScheme(final String currentSchemeURL) {
        final Thread secondary = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final URLConnection myConnection = new URL(currentSchemeURL).openConnection();
                    originScheme = BitmapFactory.decodeStream(myConnection.getInputStream());
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
        if (originScheme == null) {
            originScheme = BitmapFactory.decodeStream(getClass().getResourceAsStream(SCHEME_PATH));
        }
    }

    private int getMaxXScroll() {
        return Math.abs((originScheme.getWidth() / 2) - (displayWidth / 2));
    }

    private int getMaxYScroll() {
        return Math.abs((originScheme.getHeight() / 2) - (displayHigh / 2));
    }
}

