package ru.hypernavi.server.servlet.admin;

/**
 * Created by Константин on 19.08.2015.
 */

import org.jetbrains.annotations.NotNull;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.logging.Logger;


import com.google.common.net.MediaType;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONException;
import org.json.JSONObject;
import ru.hypernavi.core.webutil.GeocoderParser;
import ru.hypernavi.server.servlet.AbstractHttpService;


@WebServlet(name = "geoloc", value = "/geoloc")
public class AddressToPositionServlet extends AbstractHttpService {
    private static final Logger LOG = Logger.getLogger(AddressToPositionServlet.class.getName());

    @NotNull
    private final HttpClient client = HttpClientBuilder.create()
            .disableContentCompression()
            .setMaxConnPerRoute(100)
            .setMaxConnTotal(100)
            .build();

    public AddressToPositionServlet() {
    }

    private boolean isRequestCorrect(@NotNull final HttpServletRequest req) {
        final Map<String, String[]> parameters = req.getParameterMap();
        LOG.info(parameters.keySet().toString());
        return parameters.containsKey("address");
    }

    @Override
    public void process(@NotNull final HttpServletRequest req, @NotNull final HttpServletResponse resp) throws IOException {
        if (!isRequestCorrect(req)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        final String address = req.getParameter("address");
        if (address == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        HttpResponse geocode;
        try {
            final URI path = new URIBuilder()
                    .setScheme("https")
                    .setHost("geocode-maps.yandex.ru")
                    .setPath("/1.x")
                    .addParameter("geocode", address)
                    .addParameter("format", "json")
                    .build();
            geocode = client.execute(new HttpGet(path));
        } catch (URISyntaxException | ClientProtocolException ignored) {
            geocode = null;
        }

        if (geocode == null) {
            return ;
        }

        final String hypermarketsJSON = IOUtils.toString(geocode.getEntity().getContent(), StandardCharsets.UTF_8.name());
        LOG.info(hypermarketsJSON);
        if (hypermarketsJSON == null) {
            LOG.warning("Can't load from address");
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        final JSONObject a;
        try {
            a = new JSONObject(hypermarketsJSON);
        } catch (JSONException ignored) {
            LOG.warning("Invalid JSON");
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        final GeocoderParser b = new GeocoderParser();
        b.setResponce(a);

        final JSONObject obj = new JSONObject();
        try {
            obj.put("Location", b.getLocation().getLongitude() + " " + b.getLocation().getLatitude());
            obj.put("Address", b.getAddress());
        } catch (JSONException e) {
            LOG.warning(e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return ;
        }
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType(MediaType.JSON_UTF_8.type());
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        final java.io.OutputStream out = resp.getOutputStream();
        out.write(obj.toString().getBytes());
    }
}

