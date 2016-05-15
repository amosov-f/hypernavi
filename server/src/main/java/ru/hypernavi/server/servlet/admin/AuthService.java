package ru.hypernavi.server.servlet.admin;

import org.jetbrains.annotations.NotNull;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;


import ru.hypernavi.core.session.*;
import ru.hypernavi.server.servlet.HtmlPageHttpService;

/**
 * Created by amosov-f on 14.11.15.
 */
@WebServlet(name = "auth", value = "/auth")
public final class AuthService extends HtmlPageHttpService {
    @NotNull
    @Override
    protected SessionInitializer createReader(@NotNull final HttpServletRequest req) {
        return new RequestReader(req) {
            @Override
            public void validate(@NotNull final Session session) throws SessionValidationException {
                super.validate(session);
                validate(session, Property.URL);
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
        return Collections.singletonMap("url", session.demand(Property.URL));
    }
}
