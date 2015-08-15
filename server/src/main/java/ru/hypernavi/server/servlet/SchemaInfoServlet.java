package ru.hypernavi.server.servlet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import com.google.common.net.MediaType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.hypernavi.commons.Hypermarket;
import ru.hypernavi.commons.HypermarketSerializer;
import ru.hypernavi.core.HypermarketHolder;
import ru.hypernavi.util.GeoPoint;

/**
 * Created by Константин on 06.08.2015.
 */

@WebServlet(name = "schemainfo", value = "/schemainfo")
public class SchemaInfoServlet extends AbstractHttpService {
    private static final Log LOG = LogFactory.getLog(SchemaInfoServlet.class);

    @NotNull
    private final HypermarketHolder markets;

    public SchemaInfoServlet() {
        markets = HypermarketHolder.getInstance();
    }

    @Override
    public void process(@NotNull final HttpServletRequest request,
                        @NotNull final HttpServletResponse response) throws IOException {
        final Map<String, String[]> parameterMap = request.getParameterMap();
        if (!parameterMap.containsKey("lon") || !parameterMap.containsKey("lat")) {
            response.sendError(HttpServletResponse.SC_BAD_GATEWAY);
            return;
        }

        final Double longitude = Double.parseDouble(request.getParameter("lon"));
        final Double latitude = Double.parseDouble(request.getParameter("lat"));
        final GeoPoint currentPosition = new GeoPoint(latitude, longitude);

        final Hypermarket bestHypernavi = markets.getClosest(currentPosition);
        if (bestHypernavi == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        final List<Hypermarket> market = new ArrayList<>();
        market.add(bestHypernavi);

        final String json = generateJSON(market, currentPosition);
        if (json == null) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.JSON_UTF_8.type());
        final OutputStream out = response.getOutputStream();
        out.write(json.getBytes());
    }

    // TODO: take JSON to alone class
    @Nullable
    private String generateJSON(final List<Hypermarket> market, final GeoPoint possition) {
        String answer;
        try {
            final JSONObject jsonResponse = new JSONObject();
            final JSONArray hypermarkets = new JSONArray();
            for (int i = 0; i < market.size(); ++i) {
                hypermarkets.put(HypermarketSerializer.serialize(market.get(i)));
            }

            final JSONObject metainfo = new JSONObject();
            final JSONObject data = new JSONObject();

            metainfo.put("lontitude", possition.getLongitude());
            metainfo.put("latitude", possition.getLatitude());
            metainfo.put("iscorrect", true);

            data.put("hypermarkets", hypermarkets);

            jsonResponse.put("data", data);
            jsonResponse.put("meta", metainfo);

            answer = jsonResponse.toString();
        } catch (JSONException e) {
            answer = null;
            LOG.warn("Can't create JSON responce. Request: Latitude = " + possition.getLatitude()
                    + " Longitude = " + possition.getLongitude() + "\n" + e.getMessage());
        }
        return answer;
    }
}
