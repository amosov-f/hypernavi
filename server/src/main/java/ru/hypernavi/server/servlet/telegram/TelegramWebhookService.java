package ru.hypernavi.server.servlet.telegram;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import ru.hypernavi.core.session.*;
import ru.hypernavi.core.session.param.BodyParam;
import ru.hypernavi.core.session.param.Param;
import ru.hypernavi.core.telegram.api.TelegramApi;
import ru.hypernavi.core.telegram.api.Update;
import ru.hypernavi.core.telegram.update.WebhookUpdatesSource;
import ru.hypernavi.server.servlet.AbstractHttpService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * User: amosov-f
 * Date: 01.05.16
 * Time: 21:35
 */
@WebServlet(name = "telegram webhook", value = "/telegram/*")
public final class TelegramWebhookService extends AbstractHttpService {
    private static final Log LOG = LogFactory.getLog(TelegramWebhookService.class);

    private static final Param<Update> UPDATE_PARAM = new BodyParam.ObjectParam<>(Update.class, TelegramApi.gsonFactory());

    private static final Property<Update> UPDATE = new Property<>("update");

    @Inject
    private WebhookUpdatesSource updatesSource;

    @Inject
    @Named("hypernavi.telegram.bot.auth_token")
    private String authToken;

    @NotNull
    @Override
    protected SessionInitializer createReader(@NotNull final HttpServletRequest req) {
        return new RequestReader(req) {
            @Override
            public void initialize(@NotNull final Session session) {
                super.initialize(session);
                setPropertyIfPresent(session, UPDATE, UPDATE_PARAM);
            }

            @Override
            public void validate(@NotNull final Session session) throws SessionValidationException {
                super.validate(session);
                final String token = session.demand(Property.HTTP_PATH_INFO).substring(1);
                if (!token.equals(authToken)) {
                    throw new SessionValidationException.Forbidden("Invalid Telegram auth token: '" + token + "'!");
                }
                validate(session, UPDATE);
            }
        };
    }

    @Override
    public void service(@NotNull final Session session, @NotNull final HttpServletResponse resp) {
        final Update update = session.demand(UPDATE);
        update.setReceiptTimestamp(session.demand(Property.START_TIME));
        updatesSource.add(update);
    }
}
