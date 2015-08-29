package ru.hypernavi.server.servlet.admin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.io.*;
import java.net.URL;

import java.nio.file.Paths;
import java.util.Map;


import com.google.inject.Inject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ru.hypernavi.commons.Hypermarket;
import ru.hypernavi.core.ImageHash;
import ru.hypernavi.core.database.FileDataLoader;
import ru.hypernavi.core.database.HypermarketHolder;
import ru.hypernavi.server.servlet.AbstractHttpService;
import ru.hypernavi.util.GeoPoint;

/**
 * Created by Константин on 21.08.2015.
 */
@WebServlet(name = "register hypermarket", value = "/register/hypermarket")
public class RegisterHyperServlet extends AbstractHttpService {
    private static final Log LOG = LogFactory.getLog(RegisterHyperServlet.class);

    private final HypermarketHolder hypermarkets;


    @Inject
    RegisterHyperServlet(@NotNull final HypermarketHolder hypermarkets) {
        this.hypermarkets = hypermarkets;
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
        final byte[] image = loadImage(url);
        if (image == null)
            LOG.error("FUCK!");
        final String md5 = saveImage(image);
        final Hypermarket market = new Hypermarket(maxId, location, address, type, "img/" +  md5 + ".jpg");
        hypermarkets.addHypermarket(market, market.getId() + ".json");
        LOG.info("OK!");
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getOutputStream().print("OK");
    }

    private static byte[] loadImage(final String pathurl) {
        final byte[] image;
        try {
            final URL url = new URL(pathurl);
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            InputStream is = null;
            try {
                is = url.openStream();
                final byte[] byteChunk = new byte[4096];
                int n;

                while ( (n = is.read(byteChunk)) > 0 ) {
                    baos.write(byteChunk, 0, n);
                }
            }
            catch (IOException e) {
                // Perform any other exception handling that's appropriate.
            }
            finally {
                if (is != null) { is.close(); }
            }
            image = baos.toByteArray();

        } catch (IOException ignored) {
            return null;
        }
        return image;
    }


    private static String saveImage(final byte[] image) {
        final String md5;
        try {
            md5 = ImageHash.generate(image);
            final FileOutputStream out = new FileOutputStream(new File("data/img/"+md5 + ".jpg"));
            IOUtils.write(image, out);
        } catch (IOException ignored) {
            return null;
        }
        return md5;
    }
}
