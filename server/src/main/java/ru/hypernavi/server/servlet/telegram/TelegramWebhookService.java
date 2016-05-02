package ru.hypernavi.server.servlet.telegram;

import org.jetbrains.annotations.NotNull;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ru.hypernavi.core.session.*;
import ru.hypernavi.core.telegram.api.TelegramApi;
import ru.hypernavi.core.telegram.api.Update;
import ru.hypernavi.core.telegram.update.WebhookUpdatesSource;
import ru.hypernavi.server.servlet.AbstractHttpService;

/**
 * User: amosov-f
 * Date: 01.05.16
 * Time: 21:35
 */
@WebServlet(name = "telegram webhook", value = "/telegram/*")
public final class TelegramWebhookService extends AbstractHttpService {
    private static final Log LOG = LogFactory.getLog(TelegramWebhookService.class);

    @Inject
    private WebhookUpdatesSource updatesSource;

    @Inject
    public TelegramWebhookService(@Named("hypernavi.telegram.bot.auth_token") final String authToken) {
        super(new RequestReader.Factory<SessionInitializer>() {
            @NotNull
            @Override
            public SessionInitializer create(@NotNull final HttpServletRequest req) {
                return new BodyReader(req) {
                    @Override
                    public void validate(@NotNull final Session session) throws SessionValidationException {
                        super.validate(session);
                        final String token = session.demand(Property.HTTP_PATH_INFO).substring(1);
                        if (!token.equals(authToken)) {
                            throw new SessionValidationException.Forbidden("Invalid Telegram auth token: '" + token + "'!");
                        }
                    }
                };
            }
        });
    }

    @Override
    public void service(@NotNull final Session session, @NotNull final HttpServletResponse resp) throws IOException {
        final String body = session.demand(Property.HTTP_BODY_UTF8);
        LOG.debug("Recieved update from Telegram: '" + body + "'");

        final Update update = TelegramApi.gson().fromJson(body, Update.class);
        if (update.getUpdateId() == 0) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No update in request body: '" + body + "'");
            return;
        }
        updatesSource.add(update);
    }
}
