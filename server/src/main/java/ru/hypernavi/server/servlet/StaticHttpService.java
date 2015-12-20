package ru.hypernavi.server.servlet;

import org.jetbrains.annotations.NotNull;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;


import com.google.common.net.MediaType;
import com.google.inject.Inject;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import ru.hypernavi.commons.Platform;
import ru.hypernavi.core.session.Property;
import ru.hypernavi.core.session.RequestReader;
import ru.hypernavi.core.session.Session;

/**
 * Created by amosov-f on 19.12.15.
 */
@WebServlet(name = "static js", value = "/web/*")
public final class StaticHttpService extends AbstractHttpService {
    @Inject
    private Platform platform;

    @Inject
    public StaticHttpService(@NotNull final RequestReader.Factory<?> initFactory) {
        super(initFactory);
    }

    @Override
    public void service(@NotNull final Session session, @NotNull final HttpServletResponse resp) throws IOException {
        final String path = session.demand(Property.HTTP_REQUEST_URI);
        InputStream in = getClass().getResourceAsStream(path);;
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
        resp.setContentType(MediaType.JAVASCRIPT_UTF_8.type());
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        IOUtils.copy(in, resp.getOutputStream());
    }
}
