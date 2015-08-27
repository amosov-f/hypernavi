package ru.hypernavi.server.servlet;

import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;


import com.google.common.net.MediaType;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.http.HttpStatus;

/**
 * Created by amosov-f on 25.08.15.
 */
public abstract class HtmlPageHttpService extends AbstractHttpService {
    @NotNull
    private final String pathInBundle;
    @NotNull
    private final Configuration templatesConfig;

    protected HtmlPageHttpService(@NotNull final Configuration templatesConfig, @NotNull final String pathInBundle) {
        this.templatesConfig = templatesConfig;
        this.pathInBundle = pathInBundle;
    }

    @NotNull
    public Object getDataModel(@NotNull final HttpServletRequest req) {
        return new Object();
    }

    @Override
    public final void process(@NotNull final HttpServletRequest req, @NotNull final HttpServletResponse resp) throws IOException {
        final Template template = templatesConfig.getTemplate(pathInBundle);
        final ByteArrayOutputStream pageBytes = new ByteArrayOutputStream();
        try {
            template.process(getDataModel(req), new OutputStreamWriter(pageBytes, StandardCharsets.UTF_8));
        } catch (TemplateException e) {
            resp.sendError(HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
        resp.setStatus(HttpStatus.SC_OK);
        resp.setContentType(MediaType.HTML_UTF_8.toString());
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.getOutputStream().write(pageBytes.toByteArray());
    }
}
