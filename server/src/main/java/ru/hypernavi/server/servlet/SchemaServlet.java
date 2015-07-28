package ru.hypernavi.server.servlet;

import org.jetbrains.annotations.NotNull;


import ru.hypernavi.core.Hypernavi;
import ru.hypernavi.core.SchemaBuilder;
import ru.hypernavi.util.GeoPoint;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by Константин on 19.07.2015.
 */
public final class SchemaServlet extends HttpServlet {
    @NotNull
    private final List<Hypernavi> hypernavis;
    private static final double MIN_DISTANCE = 40.0;

    public SchemaServlet()
    {
        final SchemaBuilder reader = new SchemaBuilder();
        hypernavis = reader.read("/hypernavi_list.txt");
    }


    @Override
    protected void doGet(@NotNull final HttpServletRequest request,
                         @NotNull final HttpServletResponse response) throws IOException {
        final Map<String, String[]> parameterMap = request.getParameterMap();
        if (!parameterMap.containsKey("lon") || !parameterMap.containsKey("lat"))
        {
            response.sendError(HttpServletResponse.SC_BAD_GATEWAY);
            return;
        }

        final Double longitude = Double.parseDouble(request.getParameter("lon"));
        final Double latitude = Double.parseDouble(request.getParameter("lat"));
        final GeoPoint currentPosition = new GeoPoint(latitude, longitude);

        final int bestHypernavi = closestHypernavi(currentPosition);
        if (bestHypernavi == -1 || GeoPoint.distance(hypernavis.get(bestHypernavi).getCoordinate(), currentPosition) > MIN_DISTANCE)
        {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
        else {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("image/jpeg");
            final OutputStream out = response.getOutputStream();
            ImageIO.write(hypernavis.get(bestHypernavi).getSchema(), "jpg", out);
        }
    }

    private int closestHypernavi(final GeoPoint position) {
        double minDistance = Double.POSITIVE_INFINITY;
        int bestHypernavi = -1;
        for (int i = 0; i < hypernavis.size(); ++i) {
            if (GeoPoint.distance(hypernavis.get(i).getCoordinate(), position) < minDistance) {
                minDistance = GeoPoint.distance(hypernavis.get(i).getCoordinate(), position);
                bestHypernavi = i;
            }
        }
        return bestHypernavi;
    }
}
