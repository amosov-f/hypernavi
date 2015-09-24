package ru.hypernavi.server.servlet.admin;

import org.jetbrains.annotations.NotNull;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


import org.apache.commons.io.IOUtils;
import ru.hypernavi.server.servlet.AbstractHttpService;

/**
 * Created by Константин on 21.08.2015.
 */
// TODO: extend HtmlPageHttpService
@WebServlet(name = "add hyper", value = "/hypermarket")
public class HypermarketServlet extends AbstractHttpService {

    @Override
    public void process(@NotNull final HttpServletRequest req, @NotNull final HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html");
        IOUtils.copy(HypermarketServlet.class.getResourceAsStream("/hypermarket.html"), resp.getOutputStream());
    }
}
