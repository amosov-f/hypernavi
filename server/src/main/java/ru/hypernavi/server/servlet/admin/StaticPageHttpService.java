package ru.hypernavi.server.servlet.admin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


import com.google.common.net.MediaType;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import ru.hypernavi.server.servlet.AbstractHttpService;
import ru.hypernavi.util.MoreIOUtils;

/**
 * Created by amosov-f on 25.08.15.
 */
public abstract class StaticPageHttpService extends AbstractHttpService {
    @NotNull
    private final String path;
    @Nullable
    private final String pageContent;

    protected StaticPageHttpService(@NotNull final String pathToBundle, @NotNull final String pathInBundle) {
        path = pathToBundle + "/" + pathInBundle;
        if (MoreIOUtils.isClasspath(path)) {
            try {
                pageContent = IOUtils.toString(MoreIOUtils.getInputStream(path), StandardCharsets.UTF_8);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            pageContent = null;
        }
    }

    @Override
    public void process(@NotNull final HttpServletRequest req, @NotNull final HttpServletResponse resp) throws IOException {
        String content = pageContent;
        if (content == null) {
            content = IOUtils.toString(MoreIOUtils.getInputStream(path));
        }
        resp.setStatus(HttpStatus.SC_OK);
        resp.setContentType(MediaType.HTML_UTF_8.type());
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.getWriter().write(content);
    }
}
