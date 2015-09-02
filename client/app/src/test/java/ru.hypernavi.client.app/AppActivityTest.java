package ru.hypernavi.client.app;

import org.jetbrains.annotations.NotNull;


import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ZoomControls;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLocationManager;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static ru.hypernavi.util.MoreReflectionUtils.invoke;

/**
 * User: amosov-f
 * Date: 15.08.15
 * Time: 14:45
 */
@Config(sdk = 21)
@RunWith(ru.hypernavi.client.app.AppRobolectricTestRunner.class)
public final class AppActivityTest {
    private AppActivity appActivity;

    @Before
    public void setUp() {
        final Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
        intent.putExtra("enabled", true);
        appActivity = Robolectric.buildActivity(AppActivity.class).withIntent(intent).create().get();
    }

    @Test
    public void shouldHaveCorrectAppName() {
        assertThat(appActivity.getResources().getString(R.string.app_name), equalTo("app"));
    }

    @Test
    public void shouldNotBeNull() {
        assertNotNull(appActivity);

        final ZoomControls zoom = (ZoomControls) appActivity.findViewById(R.id.zoomControls1);
        assertNotNull(zoom);

        final Button button = (Button) appActivity.findViewById(R.id.button);
        assertNotNull(button);

        final ImageView imageView = (ImageView) appActivity.findViewById(R.id.imageView);
        assertNotNull(imageView);
    }

//    @Test
    public void shouldReturnTheLatestLocation() {
        final LocationManager locationManager = (LocationManager) RuntimeEnvironment.application.getSystemService(Context.LOCATION_SERVICE);
        final ShadowLocationManager shadowLocationManager = invoke(Shadows.class, "shadowOf", locationManager);
        final Location expectedLocation = location(LocationManager.GPS_PROVIDER, 30, 60);
        shadowLocationManager.simulateLocation(expectedLocation);
        //Assert.assertEquals(expectedLocation, appActivity.getLocation());
    }

    @NotNull
    private Location location(@NotNull final String provider, final double lon, final double lat) {
        final Location location = new Location(provider);
        location.setLongitude(lon);
        location.setLatitude(lat);
        location.setTime(System.currentTimeMillis());
        return location;
    }
}
