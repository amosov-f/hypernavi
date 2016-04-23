package ru.hypernavi.server.servlet.admin;

import org.jetbrains.annotations.NotNull;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;


import com.google.common.net.HttpHeaders;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.HttpGet;
import ru.hypernavi.commons.Dimension;
import ru.hypernavi.commons.*;
import ru.hypernavi.commons.Image;
import ru.hypernavi.commons.hint.Hint;
import ru.hypernavi.commons.hint.Plan;
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
        final String text = session.get(Property.TEXT);
        if (text != null) {
            switch (text) {
                case "priboy":
                    fillPriboy(session);
                    return;
                case "sheregesh":
                    fillSheregesh(session);
                    return;

            }
        }
        migrate(session);
    }

    private void migrate(@NotNull final Session session) throws UnsupportedEncodingException {
        final List<Hypermarket> hypermarkets = markets.getAll();
        for (final Hypermarket hypermarket : hypermarkets) {
            final GeoObject position = new GeoObject(hypermarket.getLine(), hypermarket.getCity(), ArrayGeoPoint.of(hypermarket.getLocation().getLongitude(), hypermarket.getLocation().getLatitude()));
            final String imageLink = "http://hypernavi.net" + hypermarket.getPath();
            final Image image = new Image(imageLink, dimensioner.getDimension(imageLink));
            final Double azimuth = hypermarket.hasOrientation() ? hypermarket.getOrientation() : null;
            final Site site = new Site(position, new Plan("Схема магазина", image, azimuth));
            put(site, session);
        }
    }

    private void fillPriboy(@NotNull final Session session) throws UnsupportedEncodingException {
        final PointMap[] points = {
                PointMap.of(ArrayGeoPoint.of(29.77691, 60.21363), new Point(1532, 536)),
                PointMap.of(ArrayGeoPoint.of(29.733273, 60.21502), new Point(313, 647)),
                PointMap.of(ArrayGeoPoint.of(29.737416, 60.211785), new Point(445, 805)),
                PointMap.of(ArrayGeoPoint.of(29.734286, 60.209901), new Point(401, 920)),
                PointMap.of(ArrayGeoPoint.of(29.749053, 60.224224), new Point(651, 40)),
                PointMap.of(ArrayGeoPoint.of(29.777747, 60.21935), new Point(1481, 185)),
                PointMap.of(ArrayGeoPoint.of(29.744552, 60.2135), new Point(631, 662))
        };
        final GeoObject position = new GeoObject(
                "Спортивно-оздоровительная база Прибой",
                "Санкт-Петербург, Зеленогорск г., просп. Ленина, 59",
                ArrayGeoPoint.of(29.71061, 60.207235)
        );
        final Hint hint = new Plan(
                "Схема лыжных трасс",
                new Image("http://www.adventureraces.ru/objects/2008-9-4/586_zelenogorsk_trasses.jpg", Dimension.of(0, 0), new Image[0]),
                0d,
                points
        );
        final Site site = new Site(position, hint);
        put(site, session);
    }

    private void fillSheregesh(@NotNull final Session session) throws UnsupportedEncodingException {
        final PointMap[] points = {
                PointMap.of(ArrayGeoPoint.of(87.864426, 52.950115), new Point(424, 314)),
                PointMap.of(ArrayGeoPoint.of(87.878242, 52.946880), new Point(404, 363)),
                PointMap.of(ArrayGeoPoint.of(87.881500, 52.936802), new Point(252, 456)),
                PointMap.of(ArrayGeoPoint.of(87.872322, 52.939671), new Point(280, 399)),
                PointMap.of(ArrayGeoPoint.of(87.886307, 52.931137), new Point(121, 536)),
                PointMap.of(ArrayGeoPoint.of(87.889999, 52.929447), new Point(134, 546)),
                PointMap.of(ArrayGeoPoint.of(87.966248, 52.947880), new Point(1271, 749)),
//                PointMap.of(ArrayGeoPoint.of(49.085281, 2.133985), new Point(949, 278)),
                PointMap.of(ArrayGeoPoint.of(87.933517, 52.954025), new Point(1032, 361)),
                PointMap.of(ArrayGeoPoint.of(87.934677, 52.969238), new Point(1540, 252)),
                PointMap.of(ArrayGeoPoint.of(87.951111, 52.952625), new Point(1603, 485)),
                PointMap.of(ArrayGeoPoint.of(87.938316, 52.954544), new Point(1415, 342)),
                PointMap.of(ArrayGeoPoint.of(87.926857, 52.951675), new Point(821, 273)),
                PointMap.of(ArrayGeoPoint.of(87.901680, 52.931507), new Point(389, 495)),
                PointMap.of(ArrayGeoPoint.of(87.968338, 52.947304), new Point(1299, 822))
        };
        final GeoObject position = new GeoObject(
                "поселок городского типа Шерегеш",
                "Россия, Кемеровская область, Таштагольский район",
                ArrayGeoPoint.of(87.989488, 52.921122)
        );
        final Hint hint = new Plan(
                "Схема лыжных трасс",
                new Image("http://nutrinews.ru/wp-content/uploads/2015/12/CHto-za-SHeregesh-i-gde-on-nahoditsya.jpg", Dimension.of(547, 397), new Image[0]),
                null,
                points
        );
        final Site site = new Site(position, hint);
        put(site, session);
    }

    private void put(@NotNull final Site site, @NotNull final Session session) throws UnsupportedEncodingException {
        final String siteValue = URLEncoder.encode(GsonUtils.gson().toJson(site), StandardCharsets.UTF_8.name());
        final HttpGet req = new HttpGet("http://hypernavi.net/admin/site/put?site=" + siteValue);
        req.setHeader(HttpHeaders.COOKIE, session.get(Property.HTTP_COOKIE));
        httpClient.execute(req, IOFunction.identity());
    }
}
