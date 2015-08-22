package ru.hypernavi.server.servlet;

import org.jetbrains.annotations.NotNull;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

/**
 * Created by Константин on 21.08.2015.
 */
@WebServlet(name = "add hyper", value = "/hypermarket")
public class HypermarketServlet extends AbstractHttpService {

    @Override
    public void process(@NotNull final HttpServletRequest req, @NotNull final HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html");
        resp.getOutputStream().write(IOUtils.toByteArray(HypermarketServlet.class.getResourceAsStream("/hypermarket.html")));
    }
}
