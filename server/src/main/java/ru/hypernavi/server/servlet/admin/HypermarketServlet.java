package ru.hypernavi.server.servlet.admin;

import org.jetbrains.annotations.NotNull;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


import com.google.inject.Inject;
import org.apache.commons.io.IOUtils;
import ru.hypernavi.core.session.RequestReader;
import ru.hypernavi.core.session.Session;
import ru.hypernavi.server.servlet.AbstractHttpService;

/**
 * Created by Константин on 21.08.2015.
 */
// TODO: extend HtmlPageHttpService
@WebServlet(name = "add hyper", value = "/hypermarket")
public class HypermarketServlet extends AbstractHttpService {
    @Inject
    protected HypermarketServlet(@NotNull final RequestReader.Factory<?> initFactory) {
        super(initFactory);
    }

    @Override
    public void service(@NotNull final Session session, @NotNull final HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html");
        IOUtils.copy(HypermarketServlet.class.getResourceAsStream("/hypermarket.html"), resp.getOutputStream());
    }
}
