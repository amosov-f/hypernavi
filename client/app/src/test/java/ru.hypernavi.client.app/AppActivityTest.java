package ru.hypernavi.client.app;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.core.IsEqual.equalTo;

/**
 * User: amosov-f
 * Date: 15.08.15
 * Time: 14:45
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = "client/app/AndroidManifest.xml", sdk = 21)
public class AppActivityTest {
    private AppActivity appActivity;

    @Before
    public void setUp() {
        appActivity = Robolectric.setupActivity(AppActivity.class);
    }

    @Test
    public void shouldHaveCorrectAppName() {
        Assert.assertThat(appActivity.getResources().getString(R.string.app_name), equalTo("app"));
    }
}
