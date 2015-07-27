package ru.hypernavi.server.servlet;

import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import ru.hypernavi.util.GeoPoint;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Константин on 19.07.2015.
 */
public class SchemaServlet extends HttpServlet {
    public SchemaServlet() throws IOException {
        readAllSchemas();
    }

    private void readAllSchemas() throws IOException {
        final List<Integer> idHyperNavi = new ArrayList<>();
        final List<Double> latitude = new ArrayList<>();
        final List<Double> longitude = new ArrayList<>();
        schemas = new ArrayList<>();
        coordinates = new ArrayList<>();

        final String initFile = IOUtils.toString(getClass().getResourceAsStream("/Okey.txt"));
        final String[] tokens = initFile.split("[ \n]+");
        for (int i = 0; i < tokens.length; i += 3) {
            idHyperNavi.add(Integer.parseInt(tokens[i]));
            latitude.add(Double.parseDouble(tokens[i + 1]));
            longitude.add(Double.parseDouble(tokens[i + 2]));
        }

        for (int i = 0; i < idHyperNavi.size(); ++i)
        {
            coordinates.add(new GeoPoint(latitude.get(i), longitude.get(i)));
            schemas.add(ImageIO.read(getClass().getResourceAsStream("/Okey_" + idHyperNavi.get(i).toString() + ".jpg")));
        }
    }

    private List<BufferedImage> schemas;
    private List<GeoPoint> coordinates;
    private static final double MIN_DISTANCE = 40.0;
    @Override
    protected void doGet(@NotNull final HttpServletRequest request,
                         @NotNull final HttpServletResponse response) throws ServletException, IOException {
        final Double longitude = Double.parseDouble(request.getParameter("longitude"));
        final Double latitude = Double.parseDouble(request.getParameter("latitude"));
        final GeoPoint currentPosition = new GeoPoint(latitude, longitude);

        final int bestHypernavi = closestHypernavi(currentPosition);
        if (bestHypernavi == -1 || GeoPoint.distance(coordinates.get(bestHypernavi), currentPosition) > MIN_DISTANCE)
        {
            response.sendError(404);
        }
        else {
            response.setContentType("image/jpeg");
            final OutputStream out = response.getOutputStream();
            ImageIO.write(schemas.get(bestHypernavi), "jpg", out);
            out.close();
        }
    }
    private int closestHypernavi(final GeoPoint position) {
        double minDistance = Double.POSITIVE_INFINITY;
        int bestHypernavi = -1;
        for (int i = 0; i < coordinates.size(); ++i) {
            if (GeoPoint.distance(coordinates.get(i), position) < minDistance) {
                minDistance = GeoPoint.distance(coordinates.get(i), position);
                bestHypernavi = i;
            }
        }
        return bestHypernavi;
    }
}
