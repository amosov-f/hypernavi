package ru.hypernavi.server.servlet;

import org.jetbrains.annotations.NotNull;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import java.io.*;

import com.google.inject.Inject;
import ru.hypernavi.commons.Hypermarket;
import ru.hypernavi.core.database.HypermarketHolder;
import ru.hypernavi.util.GeoPoint;

/**
 * Created by Константин on 02.09.2015.
 */
@WebServlet(name = "info about hypermarkets", value = "/admin")
public class HypermarketsInfoServlet extends AbstractHttpService {
    private final HypermarketHolder markets;
    private int total;
    private List<Hypermarket> list;

    @Inject
    public HypermarketsInfoServlet(@NotNull final HypermarketHolder markets) {
        this.markets = markets;
        update();
    }

    @Override
    public void process(@NotNull final HttpServletRequest req, @NotNull final HttpServletResponse resp) throws IOException {
        if (total != markets.size()) {
            update();
        }
        final OutputStream in = resp.getOutputStream();
        final String header = String.format("In database %d hypermarkets\n", total);
        byte[] bytes = header.getBytes();
        in.write(bytes);
        for (int i = 0; i < list.size(); ++i) {
            final Hypermarket market = list.get(i);
            final String info = String.format("Hypermarket #%d:\n"+"Coordinates(lon, lat): (%f, %f)\n"+"Address %s\n" +
                    "HasAngle: %s\n" + "Angle: %f\n"+"type: %s\n" + "id %d\n\n", i + 1, market.getLocation().getLongitude(),
                    market.getLocation().getLatitude(), market.getAddress(), market.hasOrientation(), market.getOrientation(),
                    market.getType(), market.getId());
            bytes = info.getBytes();
            in.write(bytes);
        }
    }

    private void update() {
        total = markets.size();
        // noinspection MagicNumber
        list = markets.getClosest(new GeoPoint(30, 60), total);
    }
}
