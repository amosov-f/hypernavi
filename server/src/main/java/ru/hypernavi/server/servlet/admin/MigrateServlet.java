package ru.hypernavi.server.servlet.admin;

import org.jetbrains.annotations.NotNull;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;


import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.HttpGet;
import ru.hypernavi.commons.*;
import ru.hypernavi.core.auth.AdminRequestReader;
import ru.hypernavi.core.database.HypermarketHolder;
import ru.hypernavi.core.http.HyperHttpClient;
import ru.hypernavi.core.session.Property;
import ru.hypernavi.core.session.RequestReader;
import ru.hypernavi.core.session.Session;
import ru.hypernavi.core.webutil.ImageDimensioner;
import ru.hypernavi.server.servlet.AbstractHttpService;
import ru.hypernavi.util.ArrayGeoPoint;
import ru.hypernavi.util.MoreReflectionUtils;
import ru.hypernavi.util.function.IOFunction;
import ru.hypernavi.util.json.GsonUtils;

/**
 * Created by Константин on 29.09.2015.
 */
@Deprecated
@WebServlet(name = "migrate", value = "/admin/migrate")
public class MigrateServlet extends AbstractHttpService {
    private static final Log LOG = LogFactory.getLog(MigrateServlet.class);
    static {
        MoreReflectionUtils.load(SearchResponse.Data.class);
    }

    @NotNull
    private final HypermarketHolder markets;
    @Inject
    private HyperHttpClient httpClient;
    @Inject
    @Named("localport")
    private int localPort;
    @Inject
    private ImageDimensioner dimensioner;

    @Inject
    public MigrateServlet(@NotNull final HypermarketHolder markets, @NotNull final RequestReader.Factory<AdminRequestReader> readerFactory) {
        super(readerFactory);
        this.markets = markets;
    }

    @Override
    public void service(@NotNull final Session session, @NotNull final HttpServletResponse resp) throws IOException {
        migrate(session);
    }

    private void migrate(@NotNull final Session session) throws UnsupportedEncodingException {
        final List<Hypermarket> hypermarkets = markets.getAll();
        for (final Hypermarket hypermarket : hypermarkets) {
            final GeoObject position = new GeoObject(hypermarket.getLine(), hypermarket.getCity(), ArrayGeoPoint.of(hypermarket.getLocation().getLongitude(), hypermarket.getLocation().getLatitude()));
            final String imageLink = "http://hypernavi.net" + hypermarket.getPath();
            final Image image = new Image(imageLink, dimensioner.getDimension(imageLink));
            final Double azimuth = hypermarket.hasOrientation() ? hypermarket.getOrientation() : null;
            final Site site = new Site(position, new Plan(image, azimuth));
            final String siteValue = URLEncoder.encode(GsonUtils.gson().toJson(site), StandardCharsets.UTF_8.name());
            final String otherParams = session.getOptional(Property.HTTP_QUERY_STRING).map(qs -> "&" + qs).orElse("");
            httpClient.execute(
                    new HttpGet("http://localhost:" + localPort + "/admin/site/put?site=" + siteValue + otherParams),
                    IOFunction.identity()
            );
        }
    }
}
