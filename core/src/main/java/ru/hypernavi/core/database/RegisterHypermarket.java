package ru.hypernavi.core.database;

import com.google.inject.Inject;
import ru.hypernavi.commons.Building;
import ru.hypernavi.commons.Hypermarket;
import ru.hypernavi.commons.WebInfo;
import ru.hypernavi.core.URLdownload;
import ru.hypernavi.util.MD5;

/**
 * Created by Константин on 16.09.2015.
 */
public final class RegisterHypermarket {
    @Inject private static HypermarketHolder hypermarkets;
    @Inject private static ImageDataBase images;

    private RegisterHypermarket() {
    }

    public static void register(final Building build, final String type, final String url, final String page) {
        final int maxId = hypermarkets.size();
        final String address = build.getAddress();
        final byte[] image = (new URLdownload()).load(url);
        final String path;

        if (image != null) {
            images.add("/img", "/" + MD5.generate(image) + ".jpg", image);
            path = "/img/" + MD5.generate(image) + ".jpg";
        } else {
            path = "/img/NotFound.jpg";
        }

        final Hypermarket market = new Hypermarket(maxId, new Building(build.getLocation(), address), type, new WebInfo(path, url, page));
        hypermarkets.addHypermarket(market, "/" + market.getId() + ".json");
    }
}
