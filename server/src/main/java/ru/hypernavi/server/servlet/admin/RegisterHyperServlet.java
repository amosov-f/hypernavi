package ru.hypernavi.server.servlet.admin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.io.*;

import java.nio.file.Paths;
import java.util.Map;


import org.apache.commons.io.IOUtils;
import ru.hypernavi.commons.Hypermarket;
import ru.hypernavi.core.ImageHash;
import ru.hypernavi.core.database.HypermarketHolder;
import ru.hypernavi.core.database.ImageLoader;
import ru.hypernavi.server.servlet.AbstractHttpService;
import ru.hypernavi.util.GeoPoint;

/**
 * Created by Константин on 21.08.2015.
 */
@WebServlet(name = "register hypermarket", value = "/register/hypermarket")
public class RegisterHyperServlet extends AbstractHttpService {
    private final HypermarketHolder hypermarkets;

    RegisterHyperServlet() {
        hypermarkets = HypermarketHolder.getInstance();
    }

    private boolean isRequestHaveKeys(@NotNull final HttpServletRequest req, @Nullable final String[] keys) {
        if (keys == null) {
            return true;
        }
        final Map<String, String[]> parameters = req.getParameterMap();
        if (parameters == null) {
            return false;
        }
        for (int i = 0; i < keys.length; ++i) {
            if (!parameters.containsKey(keys[i])) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void process(@NotNull final HttpServletRequest req, @NotNull final HttpServletResponse resp) throws IOException {
        final String[] expectedParameters = {"lon", "lat", "address", "url", "type"};
        if (!isRequestHaveKeys(req, expectedParameters)) {
            return;
        }
        final GeoPoint location = new GeoPoint(Double.parseDouble(req.getParameter("lon")),
                Double.parseDouble(req.getParameter("lat")));
        final int maxId = 0;
        final String address = req.getParameter("address");
        final String type = req.getParameter("type");
        final String url = req.getParameter("url");
        final byte[] image = loadImage(url);

        final String md5 = saveImage(image);
        final Hypermarket market = new Hypermarket(maxId, location, address, type, md5);
        //SaveHypermarket(market);
        hypermarkets.addHypermarket(market);
    }

    public static byte[] loadImage(final String path) {
        final byte[] image;
        try {
            image = Files.readAllBytes(Paths.get(path));
        } catch (IOException ignored) {
            return null;
        }
        return image;
    }


    public static String saveImage(final byte[] image) {
        final String md5;
        try {
            md5 = ImageHash.generate(image);
            final FileOutputStream out = new FileOutputStream(new File("./img/"+md5 + ".jpg"));
            IOUtils.write(image, out);
        } catch (IOException ingored) {
            return null;
        }
        return md5;
    }
}
