package ru.hypernavi.server.servlet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;


import com.google.inject.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.log4j.MDC;
import org.eclipse.jetty.server.Request;
import ru.hypernavi.core.http.HttpTools;
import ru.hypernavi.core.session.*;
import ru.hypernavi.util.json.GsonUtils;

/**
 * User: amosov-f
 * Date: 06.08.15
 * Time: 1:33
 */
public abstract class AbstractHttpService extends HttpServlet {
    private static final Log LOG = LogFactory.getLog(AbstractHttpService.class);

    @NotNull
    private final RequestReader.Factory<?> initFactory;
    @Inject
    @Nullable
    private Provider<Session> sessionFactory;

    protected AbstractHttpService(@NotNull final RequestReader.Factory<?> initFactory) {
        this.initFactory = initFactory;
    }

    @NotNull
    private static String generateRequestId() {
        final String nanos = Long.toString(System.nanoTime());
        // noinspection MagicNumber
        final int random = ThreadLocalRandom.current().nextInt(1000, 10000);
        return System.currentTimeMillis() + nanos.substring(nanos.length() - 3) + "-" + random;
    }

    @NotNull
    protected WebServlet getServiceConfig() {
        return Objects.requireNonNull(
                getClass().getAnnotation(WebServlet.class),
                "Annotate " + getClass().getSimpleName() + " as @WebServlet!"
        );
    }

    @Override
    protected void service(@NotNull final HttpServletRequest req, @NotNull final HttpServletResponse resp) throws IOException {
        final long timeStamp = System.currentTimeMillis();
        MDC.put("reqid", generateRequestId());
        LOG.info("Started processing: " + HttpTools.curl(req));

        final SessionInitializer initializer = initFactory.create(req);
        final Session session = Objects.requireNonNull(sessionFactory).get();
        initializer.initialize(session);
        try {
            initializer.validate(session);
        } catch (SessionValidationException e) {
            switch (e.getError()) {
                case BAD_REQUEST:
                    resp.sendError(HttpStatus.SC_BAD_REQUEST, e.getMessage());
                    return;
                case UNAUTHORIZED:
                    resp.sendRedirect("/auth?url=" + req.getRequestURI());
                    return;
                case FORBIDDEN:
                    resp.sendError(HttpStatus.SC_FORBIDDEN, e.getMessage());
                    return;
            }
        }

        service(session, resp);

        req.setAttribute(HttpTools.SERVICE, getServiceConfig().name());
        LOG.info(String.format(
                "Finished processing in [service: %d, jetty: %d] ms",
                System.currentTimeMillis() - timeStamp,
                System.currentTimeMillis() - ((Request) req).getTimeStamp()
        ));
    }

    public abstract void service(@NotNull final Session session, @NotNull final HttpServletResponse resp) throws IOException;

    protected final void write(@NotNull final Object obj, @NotNull final HttpServletResponse resp) throws IOException {
        resp.setStatus(HttpStatus.SC_OK);
        resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        GsonUtils.gson().toJson(obj, resp.getWriter());
    }
}
