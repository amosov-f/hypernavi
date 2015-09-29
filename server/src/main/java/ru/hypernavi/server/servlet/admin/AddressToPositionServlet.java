package ru.hypernavi.server.servlet.admin;

/**
 * Created by Константин on 19.08.2015.
 */

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.logging.Logger;


import org.apache.http.entity.ContentType;
import org.json.JSONException;
import org.json.JSONObject;
import ru.hypernavi.core.webutil.GeocoderParser;
import ru.hypernavi.core.webutil.GeocoderSender;
import ru.hypernavi.server.servlet.AbstractHttpService;


@WebServlet(name = "geoloc", value = "/geoloc")
public class AddressToPositionServlet extends AbstractHttpService {
    private static final Logger LOG = Logger.getLogger(AddressToPositionServlet.class.getName());


    private boolean isRequestCorrect(@NotNull final HttpServletRequest req) {
        final Map<String, String[]> parameters = req.getParameterMap();
        LOG.info(parameters.keySet().toString());
        return parameters.containsKey("geocode");
    }




    @Nullable
    final JSONObject getAddressPosition(@NotNull final String request) {
        final JSONObject a = GeocoderSender.getGeocoderResponse(request);
        final GeocoderParser b = new GeocoderParser();
        b.setResponce(a);

        if (b.getLocation() == null || b.getAddress() == null || b.getCity() == null || b.getLine() == null || b.getNumber() == null) {
            return null;
        }
        LOG.info(b.getLocation() + ":" + b.getCity() + ":" + b.getLine() + ":" + b.getNumber() + ":" + b.getAddress());

        final JSONObject obj = new JSONObject();
        try {
            obj.put("Location", b.getLocation().getLongitude() + " " + b.getLocation().getLatitude());
            obj.put("Address", b.getAddress());
            obj.put("City", b.getCity());
            obj.put("Line", b.getLine());
            obj.put("Number", b.getNumber());
        } catch (JSONException ignored) {
            return null;
        }
        return obj;
    }

    @Override
    public void process(@NotNull final HttpServletRequest req, @NotNull final HttpServletResponse resp) throws IOException {
        if (!isRequestCorrect(req)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        final String request = req.getParameter("geocode");
        if (request == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        final JSONObject obj = getAddressPosition(request);
        
        if (obj == null) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } else {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
            resp.getOutputStream().write(obj.toString().getBytes(StandardCharsets.UTF_8.name()));
        }
    }
}

