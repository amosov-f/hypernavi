package ru.hypernavi.server.servlet.admin;

import org.jetbrains.annotations.NotNull;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;


import com.google.common.collect.ImmutableMap;
import ru.hypernavi.core.session.*;
import ru.hypernavi.core.session.param.Param;
import ru.hypernavi.core.session.param.QueryParam;
import ru.hypernavi.server.servlet.HtmlPageHttpService;

/**
 * Created by amosov-f on 14.11.15.
 */
@WebServlet(name = "auth", value = "/auth")
public final class AuthService extends HtmlPageHttpService {
    private static final Property<String> URL = new Property<>("url");
    private static final Param<String> PARAM_URL = new QueryParam.StringParam("url");

    @NotNull
    @Override
    protected SessionInitializer createReader(@NotNull final HttpServletRequest req) {
        return new RequestReader(req) {
            @Override
            public void initialize(@NotNull final Session session) {
                setPropertyIfPresent(session, URL, PARAM_URL);
            }

            @Override
            public void validate(@NotNull final Session session) throws SessionValidationException {
                validate(session, URL);
            }
        };
    }

    @NotNull
    @Override
    public String getPathInBundle(@NotNull final Session session) {
        return "auth.ftl";
    }

    @NotNull
    @Override
    public Object toDataModel(@NotNull final Session session) {
        return new ImmutableMap.Builder<>().put("url", session.demand(URL)).build();
    }
}
