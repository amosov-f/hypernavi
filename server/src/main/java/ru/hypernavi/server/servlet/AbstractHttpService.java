package ru.hypernavi.server.servlet;

import org.jetbrains.annotations.NotNull;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpStatus;
import org.apache.log4j.MDC;
import org.eclipse.jetty.server.Request;
import ru.hypernavi.core.session.RequestReader;
import ru.hypernavi.core.session.Session;
import ru.hypernavi.core.session.SessionInitializationException;
import ru.hypernavi.core.session.SessionInitializer;
import ru.hypernavi.core.http.HttpTools;

/**
 * User: amosov-f
 * Date: 06.08.15
 * Time: 1:33
 */
public abstract class AbstractHttpService extends HttpServlet {
    private static final Log LOG = LogFactory.getLog(AbstractHttpService.class);

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

        final SessionInitializer initializer = getInitializer(req);
        final Session session = new Session();
        initializer.initialize(session);
        try {
            initializer.validate(session);
        } catch (SessionInitializationException e) {
            resp.sendError(HttpStatus.SC_BAD_REQUEST, e.getMessage());
            return;
        }

        service(session, resp);

        req.setAttribute(HttpTools.SERVICE, getServiceConfig().name());
        LOG.info(String.format(
                "Finished processing in [service: %d, jetty: %d] ms",
                System.currentTimeMillis() - timeStamp,
                System.currentTimeMillis() - ((Request) req).getTimeStamp()
        ));
    }

    @NotNull
    public SessionInitializer getInitializer(@NotNull final HttpServletRequest req) {
        return new RequestReader(req);
    }

    public abstract void service(@NotNull final Session session, @NotNull final HttpServletResponse resp) throws IOException;
}
