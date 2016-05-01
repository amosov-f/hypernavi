package ru.hypernavi.server.servlet.telegram;

import org.jetbrains.annotations.NotNull;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UncheckedIOException;


import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ru.hypernavi.core.session.RequestReader;
import ru.hypernavi.core.session.Session;
import ru.hypernavi.core.session.SessionInitializer;
import ru.hypernavi.server.servlet.AbstractHttpService;

/**
 * User: amosov-f
 * Date: 01.05.16
 * Time: 21:35
 */
@WebServlet(name = "telegram webhook", value = "/telegram/*")
public final class TelegramWebhookService extends AbstractHttpService {
    private static final Log LOG = LogFactory.getLog(TelegramWebhookService.class);

    public TelegramWebhookService() {
        super(new RequestReader.Factory<SessionInitializer>() {
            @NotNull
            @Override
            public SessionInitializer create(@NotNull final HttpServletRequest req) {
                return new RequestReader(req) {
                    @Override
                    public void initialize(@NotNull final Session session) {
                        super.initialize(session);
                        try {
                            LOG.info("data: " + IOUtils.toString(req.getInputStream()));
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    }
                };
            }
        });
    }

    @Override
    public void service(@NotNull final Session session, @NotNull final HttpServletResponse resp) {
    }
}
