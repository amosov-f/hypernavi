package ru.hypernavi.server.servlet.admin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.hypernavi.core.session.Property;
import ru.hypernavi.core.session.RequestReader;
import ru.hypernavi.core.session.Session;
import ru.hypernavi.core.session.SessionInitializer;
import ru.hypernavi.core.session.param.Param;
import ru.hypernavi.core.session.param.QueryParam;
import ru.hypernavi.server.servlet.HtmlPageHttpService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.util.HashMap;

/**
 * User: amosov-f
 * Date: 15.05.16
 * Time: 15:05
 */
@WebServlet(name = "scheme markup", value = "/admin/markup")
public final class MarkupService extends HtmlPageHttpService {
    @NotNull
    private static final Param<Point[]> PARAM_POINTS = new QueryParam.ObjectParam<>("points", Point[].class);
    @NotNull
    private static final Property<Point[]> MAP_POINTS = new Property<>("map_points");

    @NotNull
    @Override
    protected SessionInitializer createReader(@NotNull final HttpServletRequest req) {
        return new RequestReader(req) {
            @Override
            public void initialize(@NotNull final Session session) {
                super.initialize(session);
                setPropertyIfPresent(session, MAP_POINTS, PARAM_POINTS);
            }
        };
    }

    @NotNull
    @Override
    public String getPathInBundle(@NotNull final Session session) {
        return "markup.ftl";
    }

    @Nullable
    @Override
    public Object toDataModel(@NotNull final Session session) {
        final Point[] points;
        if (session.has(Property.MAP_POINT)) {
            points = new Point[]{session.demand(Property.MAP_POINT)};
        } else {
            points = session.get(MAP_POINTS, new Point[0]);
        }
        return new HashMap<String, Object>() {{
            put("link", session.get(Property.LINK));
            put("points", points);
        }};
    }
}
