package ru.hypernavi.server.servlet.admin;

import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.NotNull;
import ru.hypernavi.core.auth.AdminRequestReader;
import ru.hypernavi.core.session.Property;
import ru.hypernavi.core.session.Session;
import ru.hypernavi.core.session.SessionInitializer;
import ru.hypernavi.server.servlet.HtmlPageHttpService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Created by Константин on 02.09.2015.
 */
@WebServlet(name = "adminka", value = "/admin")
public final class AdminService extends HtmlPageHttpService {
    @NotNull
    private final LocalDateTime initTime = LocalDateTime.now(ZoneId.of("Europe/Moscow"));

    @NotNull
    @Override
    protected SessionInitializer createReader(@NotNull final HttpServletRequest req) {
        return new AdminRequestReader(req);
    }

    @NotNull
    @Override
    public String getPathInBundle(@NotNull final Session session) {
        return "admin.ftl";
    }

    @NotNull
    @Override
    public Object toDataModel(@NotNull final Session session) {
        final ImmutableMap.Builder<String, Object> dataModel = new ImmutableMap.Builder<>();
        dataModel.put("server_starts", initTime);
        dataModel.put("lang", session.demand(Property.LANG));
        return dataModel.build();
    }
}
