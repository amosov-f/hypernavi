package ru.hypernavi.server.servlet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;


import com.google.common.net.MediaType;
import com.google.inject.Inject;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.http.HttpStatus;
import ru.hypernavi.core.session.RequestReader;
import ru.hypernavi.core.session.Session;

/**
 * Created by amosov-f on 25.08.15.
 */
public abstract class HtmlPageHttpService extends AbstractHttpService {
    @Inject
    private Configuration templatesConfig;

    protected HtmlPageHttpService(@NotNull final RequestReader.Factory<?> init) {
        super(init);
    }

    @NotNull
    public abstract String getPathInBundle(@NotNull final Session session);

    @Nullable
    public Object toDataModel(@NotNull final Session session) throws TemplateException {
        return new Object();
    }

    @Override
    public final void service(@NotNull final Session session, @NotNull final HttpServletResponse resp) throws IOException {
        final Template template = templatesConfig.getTemplate(getPathInBundle(session), StandardCharsets.UTF_8.name());
        final ByteArrayOutputStream pageBytes = new ByteArrayOutputStream();
        try {
            final Object dataModel = toDataModel(session);
            if (dataModel == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            template.process(dataModel, new OutputStreamWriter(pageBytes, StandardCharsets.UTF_8));
        } catch (TemplateException e) {
            resp.sendError(HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            return;
        }
        resp.setStatus(HttpStatus.SC_OK);
        resp.setContentType(MediaType.HTML_UTF_8.toString());
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.getOutputStream().write(pageBytes.toByteArray());
    }
}
