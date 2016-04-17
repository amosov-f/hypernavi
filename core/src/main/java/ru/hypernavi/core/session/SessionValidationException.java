package ru.hypernavi.core.session;

import org.jetbrains.annotations.NotNull;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpStatus;

/**
 * Created by amosov-f on 08.11.15.
 */
public class SessionValidationException extends Exception {
    private static final Log LOG = LogFactory.getLog(SessionValidationException.class);

    public SessionValidationException(@NotNull final String detailMessage) {
        super(detailMessage);
    }

    public void accept(@NotNull final Session session, @NotNull final HttpServletResponse resp) throws IOException {
        LOG.info("Bad request: " + getMessage());
        resp.sendError(HttpStatus.SC_BAD_REQUEST, getMessage());
    }

    public static class UnAuthorized extends SessionValidationException {
        public UnAuthorized(@NotNull final String detailMessage) {
            super(detailMessage);
        }

        @Override
        public void accept(@NotNull final Session session, @NotNull final HttpServletResponse resp) throws IOException {
            LOG.info("Unauthorized: " + getMessage());
            resp.sendRedirect("/auth?url=" + session.get(Property.HTTP_REQUEST_URI));
        }
    }

    public static class Forbidden extends SessionValidationException {
        public Forbidden(@NotNull final String detailMessage) {
            super(detailMessage);
        }

        @Override
        public void accept(@NotNull final Session session, @NotNull final HttpServletResponse resp) throws IOException {
            LOG.info("Forbidden: " + getMessage());
            resp.sendError(HttpStatus.SC_FORBIDDEN, getMessage());
        }
    }

    public static class Redirect extends SessionValidationException {
        @NotNull
        private final String locaiton;
        @NotNull
        private final Cookie[] cookies;

        public Redirect(@NotNull final String detailMessage, @NotNull final String location, @NotNull final Cookie... cookies) {
            super(detailMessage);
            this.locaiton = location;
            this.cookies = cookies;
        }

        @Override
        public void accept(@NotNull final Session session, @NotNull final HttpServletResponse resp) throws IOException {
            LOG.info("Redirect: " + getMessage());
            LOG.info("Redirecting to '" + locaiton + "' with cookies " + Arrays.toString(cookies));
            Arrays.stream(cookies).forEach(resp::addCookie);
            resp.sendRedirect(locaiton);
        }
    }
}
