package ru.hypernavi.client.app;

import android.app.Application;
import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;

/**
 * Created by amosov-f on 24.10.15.
 */
@ReportsCrashes(
        httpMethod = HttpSender.Method.PUT,
        reportType = HttpSender.Type.JSON,
        formUri = "http://hypernavi.net:5984/acra-hypernavi/_design/acra-storage/_update/report",
        formUriBasicAuthLogin = "hypernavi",
        formUriBasicAuthPassword = "hypernavi"
)
public final class HyperNaviApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ACRA.init(this);
    }
}
