package ru.hypernavi.server.servlet.admin.site;

import org.jetbrains.annotations.NotNull;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


import ru.hypernavi.commons.PointMap;
import ru.hypernavi.core.session.*;
import ru.hypernavi.core.session.param.BodyParam;
import ru.hypernavi.core.session.param.Param;
import ru.hypernavi.ml.regression.map.MapProjectionImpl;

/**
 * User: amosov-f
 * Date: 11.05.16
 * Time: 21:37
 */
@WebServlet(name = "validate markup", value = "/admin/validate")
public final class ValidatePointsService extends SiteAdminService {
    private static final Property<PointMap[]> POINTS = new Property<>("points");

    private static final Param<PointMap[]> POINTS_BODY = new BodyParam.ObjectParam<>(PointMap[].class);

    @NotNull
    @Override
    protected SessionInitializer createReader(@NotNull final HttpServletRequest req) {
        return new RequestReader(req) {
            @Override
            public void initialize(@NotNull final Session session) {
                super.initialize(session);
                setPropertyIfPresent(session, POINTS, POINTS_BODY);
            }

            @Override
            public void validate(@NotNull final Session session) throws SessionValidationException {
                super.validate(session);
                validate(session, POINTS);
            }
        };
    }

    @Override
    public void service(@NotNull final Session session, @NotNull final HttpServletResponse resp) throws IOException {
        final PointMap[] points = session.demand(POINTS);
        final double[] distances = MapProjectionImpl.validate(points);
        write(distances, resp);
    }
}
