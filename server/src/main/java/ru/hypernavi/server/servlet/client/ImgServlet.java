package ru.hypernavi.server.servlet.client;

import org.jetbrains.annotations.NotNull;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ru.hypernavi.core.database.ImageDataBase;
import ru.hypernavi.core.database.ImageFileLoader;
import ru.hypernavi.core.database.ImageResourcesLoader;
import ru.hypernavi.server.servlet.AbstractHttpService;

/**
 * Created by Константин on 19.07.2015.
 */

@WebServlet(name = "img", value = "/img/*")
public final class ImgServlet extends AbstractHttpService {
    private static final Log LOG = LogFactory.getLog(ImgServlet.class);

    public ImgServlet() {
    }

    @Override
    public void process(@NotNull final HttpServletRequest request,
                        @NotNull final HttpServletResponse response) throws IOException {

        final String name = request.getPathInfo();
        final byte[] schema = images.get("data/img" + name);

        if (schema == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("image/jpeg");
        response.getOutputStream().write(schema);
    }

    @NotNull
    private final ImageDataBase<ImageFileLoader> images
            = new ImageDataBase<>(new ImageFileLoader("data/img/"));
}
