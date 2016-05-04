package ru.hypernavi.server.servlet.client;

import org.jetbrains.annotations.NotNull;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ru.hypernavi.core.database.ImageDataBase;
import ru.hypernavi.core.session.Property;
import ru.hypernavi.core.session.Session;
import ru.hypernavi.server.servlet.AbstractHttpService;

/**
 * Created by Константин on 19.07.2015.
 */

@WebServlet(name = "img", value = "/img/*")
public final class ImgServlet extends AbstractHttpService {
    private static final Log LOG = LogFactory.getLog(ImgServlet.class);

    private final String serviceImg;

    @NotNull
    private final ImageDataBase images;

    @Inject
    public ImgServlet(@Named("hypernavi.server.serviceimg") @NotNull final String serviceImg,
                      @NotNull final ImageDataBase images)
    {
        this.serviceImg = serviceImg;
        this.images = images;
    }

    @Override
    public void service(@NotNull final Session session, @NotNull final HttpServletResponse response) throws IOException {
        final String name = session.demand(Property.HTTP_PATH_INFO);
        LOG.info(serviceImg + name);
        final byte[] schema = images.get(serviceImg, name);

        if (schema == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("image/jpeg");
        response.getOutputStream().write(schema);
    }
}
