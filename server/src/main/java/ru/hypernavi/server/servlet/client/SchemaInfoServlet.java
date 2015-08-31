package ru.hypernavi.server.servlet.client;

import org.jetbrains.annotations.NotNull;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import com.google.common.net.MediaType;
import com.google.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import ru.hypernavi.commons.Hypermarket;
import ru.hypernavi.commons.InfoResponse;
import ru.hypernavi.commons.InfoResponceSerializer;
import ru.hypernavi.core.database.HypermarketHolder;
import ru.hypernavi.server.servlet.AbstractHttpService;
import ru.hypernavi.util.GeoPoint;

/**
 * Created by Константин on 06.08.2015.
 */

@WebServlet(name = "schemainfo", value = "/schemainfo")
public class SchemaInfoServlet extends AbstractHttpService {
    private static final Log LOG = LogFactory.getLog(SchemaInfoServlet.class);

    @NotNull
    private final HypermarketHolder markets;

    @Inject
    public SchemaInfoServlet(@NotNull final HypermarketHolder markets) {
        this.markets = markets;
    }

    @Override
    public void process(@NotNull final HttpServletRequest request,
                        @NotNull final HttpServletResponse response) throws IOException {
        final Map<String, String[]> parameterMap = request.getParameterMap();
        if (!parameterMap.containsKey("lon") || !parameterMap.containsKey("lat")) {
            response.sendError(HttpServletResponse.SC_BAD_GATEWAY);
            return;
        }
        final GeoPoint currentPosition;
        try {
            final Double longitude = Double.parseDouble(request.getParameter("lon"));
            final Double latitude = Double.parseDouble(request.getParameter("lat"));
            currentPosition = new GeoPoint(longitude, latitude);
        } catch (NumberFormatException e) {
            LOG.warn("Invalid format" + e.getMessage());
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        final List<Hypermarket> hypermarkets = markets.getClosest(currentPosition, 5);

        if (hypermarkets.size() == 0) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        final JSONObject json = InfoResponceSerializer.serialize(new InfoResponse(hypermarkets, currentPosition));
        if (json == null) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.JSON_UTF_8.type());
        final OutputStream out = response.getOutputStream();
        out.write(json.toString().getBytes());
    }
}
