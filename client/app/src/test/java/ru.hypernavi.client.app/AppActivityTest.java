package ru.hypernavi.client.app;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.ZoomControls;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

//import org.robolectric.RuntimeEnvironment;
//import org.robolectric.Shadows;

/**
 * User: amosov-f
 * Date: 15.08.15
 * Time: 14:45
 */
@Config(sdk = 21)
@RunWith(AppRobolectricTestRunner.class)
public final class AppActivityTest {
    private AppActivity appActivity;

    @Before
    public void setUp() {
        //appActivity = Robolectric.setupActivity(AppActivity.class);
        appActivity = Robolectric.buildActivity(AppActivity.class).create().get();
        //android.provider.Settings;
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

    /*
    @Test
    public void shouldReturnTheLatestLocation() {
        ImageView header = (ImageView) appActivity.findViewById(R.id.imageView);
        ShadowImageView shadowHeader = Shadows.shadowOf(header);
        //LocationManager locationManager = (LocationManager)
        //    Robolectric.application.getSystemService(Context.LOCATION_SERVICE);
        //ShadowLocationManager shadowLocationManager = shadowOf(locationManager);
        //Location expectedLocation = location(LocationManager.GPS_PROVIDER, 12.0, 20.0);

        //shadowLocationManager.simulateLocation(expectedLocation);
        //Location actualLocation = mainActivity.latestLocation();

        //assertEquals(expectedLocation, actualLocation);

        //appActivity.crashApplication();
        final LocationManager locationManager = (LocationManager)
            RuntimeEnvironment.application.getSystemService(Context.LOCATION_SERVICE);
        ShadowLocationManager shadowLocationManager = Shadows.shadowOf(locationManager);
        Location expectedLocation = location(LocationManager.GPS_PROVIDER, 30, 60);

        //shadowLocationManager.simulateLocation(expectedLocation);
        assertTrue(true);
        //Location actualLocation = appActivity.latestLocation();

        //assertEquals(expectedLocation, actualLocation);
    }


    private Location location(final String provider, final double longitude, final double latitude) {
        final Location location = new Location(provider);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setTime(System.currentTimeMillis());
        return location;
    }
    */

}
