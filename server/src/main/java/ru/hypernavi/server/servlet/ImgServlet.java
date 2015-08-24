package ru.hypernavi.server.servlet;

import org.jetbrains.annotations.NotNull;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


import ru.hypernavi.core.FileImgLoader;
import ru.hypernavi.core.ImgHolder;
import ru.hypernavi.core.ResoursesImgData;

/**
 * Created by Константин on 19.07.2015.
 */

@WebServlet(name = "img", value = "/img/*")
public final class ImgServlet extends AbstractHttpService {
    public ImgServlet() {
    }

    @Override
    public void process(@NotNull final HttpServletRequest request,
                        @NotNull final HttpServletResponse response) throws IOException {

        final String md5hash = request.getPathInfo();
        final byte[] schema = images.getImage(md5hash);

        if (schema == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("image/jpeg");
        response.getOutputStream().write(schema);
    }

    @NotNull
    private final ImgHolder images = new FileImgLoader();
}
