package ru.hypernavi.server.servlet.admin;

import org.jetbrains.annotations.NotNull;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Function;


import com.google.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.HttpGet;
import ru.hypernavi.commons.GeoObject;
import ru.hypernavi.commons.Hypermarket;
import ru.hypernavi.commons.Plan;
import ru.hypernavi.commons.Site;
import ru.hypernavi.core.database.HypermarketHolder;
import ru.hypernavi.core.http.HyperHttpClient;
import ru.hypernavi.core.session.RequestReader;
import ru.hypernavi.core.session.Session;
import ru.hypernavi.server.servlet.AbstractHttpService;
import ru.hypernavi.util.ArrayGeoPoint;
import ru.hypernavi.util.json.GsonUtils;

/**
 * Created by Константин on 29.09.2015.
 */
@WebServlet(name = "migrate", value = "/admin/migrate")
public class MigrateServlet extends AbstractHttpService {
    private static final Log LOG = LogFactory.getLog(MigrateServlet.class);

    @NotNull
    private final HypermarketHolder markets;
    @Inject
    private HyperHttpClient httpClient;

    @Inject
    public MigrateServlet(@NotNull final HypermarketHolder markets, @NotNull final RequestReader.Factory<?> readerFactory) {
        super(readerFactory);
        this.markets = markets;
    }

    @Override
    public void service(@NotNull final Session session, @NotNull final HttpServletResponse resp) throws IOException {
        migrate();
    }

    private void migrate() throws UnsupportedEncodingException {
        try {
            Class.forName("ru.hypernavi.commons.SearchResponse$Data");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        final List<Hypermarket> hypermarkets = markets.getAll();
        for (final Hypermarket hypermarket : hypermarkets) {
            final GeoObject position = new GeoObject(hypermarket.getLine(), hypermarket.getCity(), ArrayGeoPoint.of(hypermarket.getLocation().getLongitude(), hypermarket.getLocation().getLatitude()));
            final Site site = new Site(position, new Plan("http://hypernavi.net" + hypermarket.getPath(), hypermarket.hasOrientation() ? hypermarket.getOrientation() : null));
            httpClient.execute(
                    new HttpGet("http://localhost:8080/admin/site/add?site=" + URLEncoder.encode(GsonUtils.gson().toJson(site), StandardCharsets.UTF_8.name())),
                    Function.identity()
            );
        }
    }
}
