package ru.hypernavi.server.servlet.admin;

import org.jetbrains.annotations.NotNull;
import ru.hypernavi.core.auth.VkRequiredAuthRequestReader;
import ru.hypernavi.core.session.Property;
import ru.hypernavi.core.session.Session;
import ru.hypernavi.core.session.SessionInitializer;
import ru.hypernavi.server.servlet.AbstractHttpService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by amosov-f on 14.06.16.
 */
@WebServlet(name = "set auth", value = "/setauth")
public class SetAuthService extends AbstractHttpService {
    @NotNull
    @Override
    protected SessionInitializer createReader(@NotNull final HttpServletRequest req) {
        return new VkRequiredAuthRequestReader(req);
    }

    @Override
    public void service(@NotNull final Session session, @NotNull final HttpServletResponse resp) throws IOException {
        resp.sendRedirect(session.demand(Property.URL));
    }
}
