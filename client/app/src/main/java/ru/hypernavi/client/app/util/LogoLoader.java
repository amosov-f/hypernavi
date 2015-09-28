package ru.hypernavi.client.app.util;

import org.jetbrains.annotations.NotNull;


import java.util.logging.Logger;


import android.graphics.BitmapFactory;
import android.widget.ImageView;
import ru.hypernavi.client.app.AppActivity;

/**
 * Created by Acer on 28.09.2015.
 */
public class LogoLoader {
    private static final Logger LOG = Logger.getLogger(LogoLoader.class.getName());
    private static final String OKEY_PATH = "/logo_okey.png";

    public void loadLogo(@NotNull final String typeOfMarket, @NotNull final ImageView logoView) {
        if (typeOfMarket.equals("Okey")) {
            LOG.info("LOGO!");
            logoView.setImageBitmap(BitmapFactory.decodeStream(getClass().getResourceAsStream(OKEY_PATH)));
        } else {
            LOG.warning("Type of market is not Okey: " + typeOfMarket);
        }
    }
}
