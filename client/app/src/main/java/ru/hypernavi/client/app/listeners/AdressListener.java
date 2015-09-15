package ru.hypernavi.client.app.listeners;

import java.util.logging.Logger;


import android.content.Intent;
import android.net.Uri;
import android.view.View;
import ru.hypernavi.client.app.AppActivity;
import ru.hypernavi.util.GeoPoint;

/**
 * Created by Acer on 15.09.2015.
 */
public class AdressListener implements View.OnClickListener {
    private static final Logger LOG = Logger.getLogger(AdressListener.class.getName());

    private AppActivity myAppActivity;

    public AdressListener(final AppActivity appActivity) {
        myAppActivity = appActivity;
    }

    // 59.940499 30.314803
    // https://maps.yandex.ru/?text=59.941%2030.313497&sll=30.313416%2C59.939050&z=15

    @Override
    public void onClick(final View v) {
        final GeoPoint closestMarketLocation = myAppActivity.getClosestMarketLocation();
        if (closestMarketLocation == null) {
            return;
        }
        final Uri requestToYandex = new Uri.Builder()
            .scheme("https")
            .authority("maps.yandex.ru")
            .appendQueryParameter("text", closestMarketLocation.getLatitude() + " " + closestMarketLocation.getLongitude())
            .appendQueryParameter("results", "1")
            .build();
        LOG.info(requestToYandex + "");
        final Intent browserIntent = new Intent(Intent.ACTION_VIEW, requestToYandex);
        myAppActivity.startActivity(browserIntent);
    }
}
