package ru.hyper.marker;

import android.app.Activity;
import android.content.Context;
import android.graphics.*;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import org.jetbrains.annotations.NotNull;
import ru.hyper.util.GeoPoint;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class MarkerActivity extends Activity {
    private static final String SCHEME_PATH = "/okey/okey_tallinskoe_shema_magazina_b.jpg";
    private static final int N_LOCATIONS = 5;

    @NotNull
    private List<Location> locations = new ArrayList<>();

    private Point schemePosition;
    private GeoPoint geoPosition;

    private boolean updatingGeoPosition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);

        final Bitmap scheme = BitmapFactory.decodeStream(getClass().getResourceAsStream(SCHEME_PATH));

        final ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageBitmap(scheme);

        System.out.println(getFilesDir());

        System.out.println(Environment.getExternalStorageDirectory());

        final PrintWriter writer;
        try {
            writer = new PrintWriter(new FileOutputStream(new File(Environment.getExternalStorageDirectory(), "hypermarker.txt"), true), true);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        writer.println();
        writer.println(SCHEME_PATH);

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (updatingGeoPosition) {
                    return false;
                }

                final Matrix inverse = new Matrix();
                imageView.getImageMatrix().invert(inverse);
                final float[] schemePoint = new float[]{event.getX(), event.getY()};
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
                canvas.drawCircle(x, y, 50, paint);
                canvas.drawPoint(x, y, paint);
                schemePosition = new Point(x, y);

                imageView.setImageBitmap(image);
                return true;
            }
        });

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("onClick");
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

                locations.clear();
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                    @Override
                    public void onLocationChanged(@NotNull final Location location) {
                        System.out.println("onLocationChanged");
                        locations.add(location);
                        if (locations.size() == N_LOCATIONS) {
                            locationManager.removeUpdates(this);

                            updateGeoPosition();

                            findViewById(R.id.button).setVisibility(View.VISIBLE);
                            findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);

                            updatingGeoPosition = false;

                            System.out.println(schemePosition + " -> " + geoPosition);
                            writer.println(System.currentTimeMillis() + " " + schemePosition.x + " " + schemePosition.y + " " + geoPosition.getLatitude() + " " + geoPosition.getLongitude());
                            Toast.makeText(MarkerActivity.this, schemePosition + " -> " + geoPosition, Toast.LENGTH_SHORT).show();
                        }

                        /*Toast.makeText(
                                MarkerActivity.this,
                                String.format("Accuracy %.2f meters, based on %d measurments", getAccuracy(), locations.size()),
                                Toast.LENGTH_SHORT
                        ).show();
                        if (locations.size() >= N_LOCATIONS) {
                            locations.remove(0);
                        }*/
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
                });
            }
        });
    }

    private void updateGeoPosition() {
        double latitude = 0;
        double longitude = 0;
        for (final Location location : locations) {
            latitude += location.getLatitude();
            longitude += location.getLongitude();
        }
        latitude /= locations.size();
        longitude /= locations.size();

        geoPosition = new GeoPoint(latitude, longitude);
    }

    /*private double getAccuracy() {
        final List<Double> latitudes = new ArrayList<>();
        final List<Double> longitudes = new ArrayList<>();
        for (final Location location : locations) {
            latitudes.add(location.getLatitude());
            longitudes.add(location.getLongitude());
        }
        final double stdevLatitude = new StandardDeviation().evaluate(Doubles.toArray(latitudes));
        final double stdevLongitude = new StandardDeviation().evaluate(Doubles.toArray(longitudes));
        final double r = (stdevLatitude + stdevLongitude) / 2;
        return r * METERS_ON_DEGREE;
    }*/
}

