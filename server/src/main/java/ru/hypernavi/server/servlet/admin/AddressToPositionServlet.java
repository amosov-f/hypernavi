package ru.hypernavi.server.servlet.admin;

/**
 * Created by Константин on 19.08.2015.
 */

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;


import org.apache.http.entity.ContentType;
import org.json.JSONException;
import org.json.JSONObject;
import ru.hypernavi.core.session.Property;
import ru.hypernavi.core.session.RequestReader;
import ru.hypernavi.core.session.Session;
import ru.hypernavi.core.session.SessionValidationException;
import ru.hypernavi.core.session.param.Param;
import ru.hypernavi.core.session.param.QueryParam;
import ru.hypernavi.core.webutil.GeocoderParser;
import ru.hypernavi.core.webutil.GeocoderSender;
import ru.hypernavi.server.servlet.AbstractHttpService;


@Deprecated
@WebServlet(name = "geoloc", value = "/geoloc")
public class AddressToPositionServlet extends AbstractHttpService {
    private static final Logger LOG = Logger.getLogger(AddressToPositionServlet.class.getName());

    private static final Param<String> PARAM_GEOCODE = new QueryParam.StringParam("geocode");
    private static final Property<String> GEOCODE = new Property<>("geocode");

    public AddressToPositionServlet() {
        super(req -> new RequestReader(req) {
            @Override
            public void initialize(@NotNull final Session session) {
                setPropertyIfPresent(session, GEOCODE, PARAM_GEOCODE);
            }

            @Override
            public void validate(@NotNull final Session session) throws SessionValidationException {
                validate(session, GEOCODE);
            }
        });
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
    public void service(@NotNull final Session session, @NotNull final HttpServletResponse resp) throws IOException {
        final String request = session.demand(GEOCODE);

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

