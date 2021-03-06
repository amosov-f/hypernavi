package ru.hypernavi.server.servlet.client;

import org.jetbrains.annotations.NotNull;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;


import com.google.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.entity.ContentType;
import org.json.JSONObject;
import ru.hypernavi.commons.Hypermarket;
import ru.hypernavi.commons.InfoResponceSerializer;
import ru.hypernavi.commons.InfoResponse;
import ru.hypernavi.core.database.HypermarketHolder;
import ru.hypernavi.core.session.Property;
import ru.hypernavi.core.session.Session;
import ru.hypernavi.core.session.SessionInitializer;
import ru.hypernavi.server.servlet.AbstractHttpService;
import ru.hypernavi.server.servlet.search.SearchRequest;
import ru.hypernavi.util.GeoPoint;

/**
 * Created by Константин on 06.08.2015.
 */

@WebServlet(name = "schemainfo", value = "/schemainfo")
public class SchemaInfoService extends AbstractHttpService {
    private static final Log LOG = LogFactory.getLog(SchemaInfoService.class);

    @Inject
    private HypermarketHolder markets;

    @NotNull
    @Override
    protected SessionInitializer createReader(@NotNull final HttpServletRequest req) {
        return new SearchRequest(req);
    }

    @Override
    public void service(@NotNull final Session session, @NotNull final HttpServletResponse response) throws IOException {
        final GeoPoint currentPosition = session.get(Property.GEO_LOCATION);

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
        response.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getOutputStream().write(json.toString().getBytes(StandardCharsets.UTF_8.name()));
    }
}
