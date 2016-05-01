package ru.hypernavi.core.session;

import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


import com.google.inject.assistedinject.Assisted;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * User: amosov-f
 * Date: 02.05.16
 * Time: 0:04
 */
public class BodyReader extends RequestReader {
    private static final Log LOG = LogFactory.getLog(BodyReader.class);

    public BodyReader(@Assisted @NotNull final HttpServletRequest req) {
        super(req);
    }

    @Override
    public void initialize(@NotNull final Session session) {
        super.initialize(session);
        final byte[] body;
        try {
            body = IOUtils.toByteArray(req.getInputStream());
        } catch (IOException e) {
            LOG.warn(e);
            return;
        }
        session.set(Property.HTTP_BODY, body);
        session.set(Property.HTTP_BODY_UTF8, new String(body, StandardCharsets.UTF_8));
    }

    @Override
    public void validate(@NotNull final Session session) throws SessionValidationException {
        if (!session.has(Property.HTTP_BODY) || !session.has(Property.HTTP_BODY_UTF8)) {
            throw new SessionValidationException("Can't read request body!");
        }
    }
}
