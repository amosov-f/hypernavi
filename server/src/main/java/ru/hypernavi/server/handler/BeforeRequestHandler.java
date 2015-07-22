package ru.hypernavi.server.handler;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHeaders;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.jetbrains.annotations.NotNull;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * User: amosov-f
 * Date: 23.07.15
 * Time: 1:29
 */
public final class BeforeRequestHandler extends AbstractHandler {
    private static final Log LOG = LogFactory.getLog(BeforeRequestHandler.class);

    @Override
    public void handle(@NotNull final String target,
                       @NotNull final Request request,
                       @NotNull final HttpServletRequest req,
                       @NotNull final HttpServletResponse resp) throws IOException, ServletException
    {
        if (req.getCharacterEncoding() == null && HttpMethod.POST.is(req.getMethod())) {
            req.setCharacterEncoding(StandardCharsets.UTF_8.name());
        }

        final long readingTimestamp = System.currentTimeMillis();
        if (HttpMethod.POST.is(req.getMethod()) || HttpMethod.PUT.is(req.getMethod())) {
            // trigger urlencoded post data upload from client
            req.getParameterMap();
        }

        final String acceptEncoding = req.getHeader(HttpHeaders.ACCEPT_ENCODING);
        LOG.info(String.format(
                "Received %s request '%s' (source=%s,%s; gzip_deflate=%b; length=%s; read_time_ms=%s; connection_id=%s)",
                req.getMethod(),
                req.getScheme() + ":" + request.getHttpURI(),
                req.getRemoteAddr(),
                req.getRemotePort(),
                StringUtils.contains(acceptEncoding, "gzip") || StringUtils.contains(acceptEncoding, "deflate"),
                req.getContentLength(),
                System.currentTimeMillis() - readingTimestamp,
                Integer.toHexString(req.getAttribute("org.eclipse.jetty.server.HttpConnection").hashCode())
        ));
    }
}
