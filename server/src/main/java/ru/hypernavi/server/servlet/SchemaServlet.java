package ru.hypernavi.server.servlet;

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
import java.nio.file.Path;
import java.nio.file.Paths;
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
        List<Integer> idHyperNavi = new ArrayList<>();
        List<Double> latitude = new ArrayList<>();
        List<Double> longitude = new ArrayList<>();
        schemas = new ArrayList<>();
        coordinates = new ArrayList<>();
        try
        {
            Scanner scanner = new Scanner("/Okey.txt");
            while (scanner.hasNext()) {
                if (scanner.hasNextInt()) {
                    idHyperNavi.add(scanner.nextInt());
                } else {
                    latitude.add(scanner.nextDouble());
                    longitude.add(scanner.nextDouble());
                }
            }
        }
        catch (Exception e)
        {
            System.out.println("No such file");
        }

        for (int i = 0; i < idHyperNavi.size(); ++i)
        {
            coordinates.add(new GeoPoint(longitude.get(i), latitude.get(i)));
            File f = new File("/Okey_" + idHyperNavi.get(i).toString() + ".jpeg");
            schemas.add(ImageIO.read(f));
        }
        System.out.println("Finished reading images");
        System.out.println(schemas.size());

    }

    private List<BufferedImage> schemas;
    private List<GeoPoint> coordinates;
    private final double MIN_DISTASNCE = 400.0;
    @Override
    protected void doGet(@NotNull final HttpServletRequest request,
                         @NotNull final HttpServletResponse response) throws ServletException, IOException {
        final Double longitude = Double.parseDouble(request.getParameter("longitude"));
        final Double latitude = Double.parseDouble(request.getParameter("latitude"));
        final GeoPoint currentPosition = new GeoPoint(longitude, latitude);

        int bestHypernavi = closestHypernavi(currentPosition);
        if (GeoPoint.distance(coordinates.get(bestHypernavi), currentPosition) > MIN_DISTASNCE)
        {
            response.sendError(404);
        }
        else {
            response.setContentType("image/jpeg");
            OutputStream out = response.getOutputStream();
            ImageIO.write(schemas.get(bestHypernavi), "jpg", out);
            out.close();
        }
    }
    private int closestHypernavi(GeoPoint position) {
        double minDistance = Double.POSITIVE_INFINITY;
        int bestHypernavi = 0;
        for (int i = 0; i < coordinates.size(); ++i) {
            if (GeoPoint.distance(coordinates.get(i), position) < minDistance) {
                minDistance = GeoPoint.distance(coordinates.get(i), position);
                bestHypernavi = i;
            }
        }
        return bestHypernavi;
    }
}
