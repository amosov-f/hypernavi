package ru.hypernavi.server.servlet.admin;

import org.jetbrains.annotations.NotNull;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;


import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import freemarker.template.Configuration;
import ru.hypernavi.core.session.*;
import ru.hypernavi.server.servlet.HtmlPageHttpService;

/**
 * Created by amosov-f on 14.11.15.
 */
@WebServlet(name = "auth", value = "/auth")
public final class AuthService extends HtmlPageHttpService {
    private static final Property<String> URL = new Property<>("url");
    private static final RequestParam<String> PARAM_URL = new RequestParam.StringParam("url");

    @Inject
    public AuthService(@NotNull final Configuration templatesConfig) {
        super(templatesConfig, "auth.ftl");
    }

    @NotNull
    @Override
    public SessionInitializer getInitializer(@NotNull final HttpServletRequest req) {
        return new RequestReader(req) {
            @Override
            public void initialize(@NotNull final Session session) {
                setPropertyIfPresent(session, URL, PARAM_URL);
            }

            @Override
            public void validate(@NotNull final Session session) throws SessionInitializationException {
                if (!session.has(URL)) {
                    throw new SessionInitializationException("No 'url' param in request!");
                }
            }
        };
    }

    @NotNull
    @Override
    public Object getDataModel(@NotNull final Session session) {
        return new ImmutableMap.Builder<>().put("url", session.demand(URL)).build();
    }
}
