package ru.hypernavi.server.servlet.admin;

import org.jetbrains.annotations.NotNull;
import ru.hypernavi.commons.PointMap;
import ru.hypernavi.core.session.*;
import ru.hypernavi.core.session.param.BodyParam;
import ru.hypernavi.core.session.param.Param;
import ru.hypernavi.ml.regression.map.MapProjectionImpl;
import ru.hypernavi.ml.regression.map.ValidationResult;
import ru.hypernavi.server.servlet.admin.site.SiteAdminService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
        return new ParamRequestReader(new RequestReader(req), POINTS, POINTS_BODY);
    }

    @Override
    public void service(@NotNull final Session session, @NotNull final HttpServletResponse resp) throws IOException {
        final PointMap[] points = session.demand(POINTS);
        final ValidationResult result = MapProjectionImpl.validate(points);
        write(result, resp);
    }
}
