package ru.hypernavi.server.servlet;

import org.jetbrains.annotations.NotNull;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;


import com.google.inject.Inject;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import ru.hypernavi.core.server.Platform;
import ru.hypernavi.core.session.Property;
import ru.hypernavi.core.session.Session;

/**
 * Created by amosov-f on 19.12.15.
 */
@WebServlet(name = "static files from resources", value = "/web/*")
public final class StaticHttpService extends AbstractHttpService {
    @Inject
    private Platform platform;

    @Override
    public void service(@NotNull final Session session, @NotNull final HttpServletResponse resp) throws IOException {
        final String path = URLDecoder.decode(session.demand(Property.HTTP_REQUEST_URI), StandardCharsets.UTF_8.name());
        InputStream in = getClass().getResourceAsStream(path);
        if (platform == Platform.DEVELOPMENT) {
            try {
                in = new FileInputStream("server/src/main/resources" + path);
            } catch (FileNotFoundException ignored) {
                in = null;
            }
        }
        if (in == null) {
            resp.sendError(HttpStatus.SC_NOT_FOUND);
            return;
        }
        resp.setStatus(HttpStatus.SC_OK);
        resp.setContentType(URLConnection.guessContentTypeFromName(path));
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        IOUtils.copy(in, resp.getOutputStream());
    }
}
