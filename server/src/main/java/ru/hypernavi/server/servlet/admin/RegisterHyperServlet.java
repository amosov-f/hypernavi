package ru.hypernavi.server.servlet.admin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.*;
import java.net.URL;

import java.util.Map;


import com.google.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ru.hypernavi.commons.Hypermarket;
import ru.hypernavi.core.ImageHash;
import ru.hypernavi.core.URLdownload;
import ru.hypernavi.core.database.HypermarketHolder;
import ru.hypernavi.core.database.ImageDataBase;
import ru.hypernavi.server.servlet.AbstractHttpService;
import ru.hypernavi.util.GeoPoint;

/**
 * Created by Константин on 21.08.2015.
 */
@WebServlet(name = "register hypermarket", value = "/register/hypermarket")
public class RegisterHyperServlet extends AbstractHttpService {
    private static final Log LOG = LogFactory.getLog(RegisterHyperServlet.class);

    private final HypermarketHolder hypermarkets;
    private final ImageDataBase images;

    @Inject
    RegisterHyperServlet(@NotNull final HypermarketHolder hypermarkets, @NotNull final ImageDataBase images) {
        this.hypermarkets = hypermarkets;
        this.images = images;
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
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        final GeoPoint location = new GeoPoint(Double.parseDouble(req.getParameter("lon")),
                Double.parseDouble(req.getParameter("lat")));

        final int maxId = hypermarkets.size();
        final String address = req.getParameter("address");
        final String type = req.getParameter("type");
        final String url = req.getParameter("url");
        final byte[] image = (new URLdownload()).load(url);
        final String path;
        if (image != null) {
            images.add("/img", "/" + ImageHash.generate(image) + ".jpg", image);
            path = "/img/" + ImageHash.generate(image) + ".jpg";
        } else {
            path = "/img/NotFound.jpg";
        }

        final Hypermarket market = new Hypermarket(maxId, location, address, type, path);
        hypermarkets.addHypermarket(market,"/" + market.getId() + ".json");

        LOG.info("OK!");
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getOutputStream().print("OK");
    }
}
