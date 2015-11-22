package ru.hypernavi.server.servlet.admin;

import org.jetbrains.annotations.NotNull;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;


import com.google.common.collect.ImmutableMap;
import ru.hypernavi.core.session.*;
import ru.hypernavi.server.servlet.HtmlPageHttpService;

/**
 * Created by amosov-f on 14.11.15.
 */
@WebServlet(name = "auth", value = "/auth")
public final class AuthService extends HtmlPageHttpService {
    private static final Property<String> URL = new Property<>("url");
    private static final RequestParam<String> PARAM_URL = new RequestParam.StringParam("url");

    public AuthService() {
        super("auth.ftl", new RequestReader.Factory<RequestReader>() {
            @NotNull
            @Override
            public RequestReader create(@NotNull final HttpServletRequest req) {
                return new RequestReader(req) {
                    @Override
                    public void initialize(@NotNull final Session session) {
                        setPropertyIfPresent(session, URL, PARAM_URL);
                    }

                    @Override
                    public void validate(@NotNull final Session session) throws SessionValidationException {
                        if (!session.has(URL)) {
                            throw new SessionValidationException("No 'url' param in request!");
                        }
                    }
                };
            }
        });
    }

    @NotNull
    @Override
    public Object getDataModel(@NotNull final Session session) {
        return new ImmutableMap.Builder<>().put("url", session.demand(URL)).build();
    }
}
