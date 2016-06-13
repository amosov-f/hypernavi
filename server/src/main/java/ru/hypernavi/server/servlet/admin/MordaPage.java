package ru.hypernavi.server.servlet.admin;

import org.jetbrains.annotations.NotNull;
import ru.hypernavi.core.auth.VkAuthRequestReader;
import ru.hypernavi.core.session.Property;
import ru.hypernavi.core.session.Session;
import ru.hypernavi.core.session.SessionInitializer;
import ru.hypernavi.server.servlet.HtmlPageHttpService;
import ru.hypernavi.util.ArrayGeoPoint;
import ru.hypernavi.util.GeoPoint;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Константин on 02.09.2015.
 */
@WebServlet(name = "morda", value = {"", "/", "/admin"})
public final class MordaPage extends HtmlPageHttpService {
    private static final GeoPoint DEFAULT_CENTER = ArrayGeoPoint.of(30.1466, 59.796);
    private static final int DEFAULT_ZOOM = 9;

    @NotNull
    private final LocalDateTime initTime = LocalDateTime.now(ZoneId.of("Europe/Moscow"));

    @NotNull
    @Override
    protected SessionInitializer createReader(@NotNull final HttpServletRequest req) {
        return new VkAuthRequestReader(req);
    }

    @NotNull
    @Override
    public String getPathInBundle(@NotNull final Session session) {
        return "morda.ftl";
    }

    @NotNull
    @Override
    public Object toDataModel(@NotNull final Session session) {
        final Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("server_starts", initTime);
        dataModel.put("vk_user", session.get(Property.VK_USER));
        dataModel.put("is_admin", session.demand(Property.IS_ADMIN));
        dataModel.put("lang", session.demand(Property.LANG));
        dataModel.put("center", session.get(Property.GEO_LOCATION, DEFAULT_CENTER));
        dataModel.put("zoom", session.get(Property.ZOOM, DEFAULT_ZOOM));
        return dataModel;
    }
}
