package ru.hypernavi.server.servlet;

import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;


import ru.hypernavi.core.Hypermarket;
import ru.hypernavi.core.HypermarketHolder;

/**
 * Created by Константин on 19.07.2015.
 */

@WebServlet(name = "img", value = "/img/*")
public final class ImgServlet extends AbstractHttpService {
    public ImgServlet() {
        markets = HypermarketHolder.getInstance();
    }

    @Override
    public void process(@NotNull final HttpServletRequest request,
                        @NotNull final HttpServletResponse response) throws IOException {

        final String md5hash = request.getPathInfo();
        final Hypermarket bestHypernavi = markets.getMD5(md5hash);
        if (bestHypernavi == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
        else {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("image/jpeg");
            final OutputStream out = response.getOutputStream();
            ImageIO.write(bestHypernavi.getSchema(), "jpg", out);
        }
    }

    @NotNull
    private HypermarketHolder markets;
}
