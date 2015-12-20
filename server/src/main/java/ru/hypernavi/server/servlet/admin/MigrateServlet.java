package ru.hypernavi.server.servlet.admin;

import org.jetbrains.annotations.NotNull;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import com.google.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import ru.hypernavi.commons.Building;
import ru.hypernavi.commons.Hypermarket;
import ru.hypernavi.core.database.HypermarketHolder;
import ru.hypernavi.core.session.RequestReader;
import ru.hypernavi.core.session.Session;
import ru.hypernavi.core.webutil.GeocoderParser;
import ru.hypernavi.core.webutil.GeocoderSender;
import ru.hypernavi.server.servlet.AbstractHttpService;
import ru.hypernavi.util.GeoPointImpl;

/**
 * Created by Константин on 29.09.2015.
 */
@WebServlet(name = "migrate", value = "/admin/migrate")
public class MigrateServlet extends AbstractHttpService {
    private static final Log LOG = LogFactory.getLog(MigrateServlet.class);

    @NotNull
    private final HypermarketHolder markets;

    @Inject
    public MigrateServlet(@NotNull final HypermarketHolder markets, @NotNull final RequestReader.Factory<?> readerFactory) {
        super(readerFactory);
        this.markets = markets;
    }

    @Override
    public void service(@NotNull final Session session, @NotNull final HttpServletResponse resp) throws IOException {
        migrate();
    }

    private void migrate() {
        final List<Hypermarket> hypermarkets = new ArrayList<>(markets.getAll());
        for (final Hypermarket hypermarket : hypermarkets) {
            final int id = hypermarket.getId();
            final GeoPointImpl loc = hypermarket.getLocation();
            final JSONObject obj = GeocoderSender.getGeocoderResponse(loc.getLongitude() + "," + loc.getLatitude());
            final GeocoderParser b = new GeocoderParser();
            b.setResponce(obj);

            final String city = b.getCity();
            final String line = b.getLine();
            final String number = b.getNumber();

            final Building build;
            if (hypermarket.hasOrientation()) {
                build = new Building(hypermarket.getLocation(), hypermarket.getAddress(), hypermarket.getOrientation(), city, line, number);
            } else {
                build = new Building(hypermarket.getLocation(), hypermarket.getAddress(), city, line, number);
            }
            markets.edit(id, new Hypermarket(id, build, hypermarket.getType(), hypermarket.getWeb()));
        }
    }
}
