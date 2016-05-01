package ru.hypernavi.server.servlet.telegram;

import org.jetbrains.annotations.NotNull;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.google.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ru.hypernavi.core.session.*;
import ru.hypernavi.core.telegram.api.Update;
import ru.hypernavi.core.telegram.update.WebhookUpdatesSource;
import ru.hypernavi.server.servlet.AbstractHttpService;
import ru.hypernavi.util.json.GsonUtils;

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

    public TelegramWebhookService() {
        super(new RequestReader.Factory<SessionInitializer>() {
            @NotNull
            @Override
            public SessionInitializer create(@NotNull final HttpServletRequest req) {
                return new BodyReader(req);
            }
        });
    }

    @Override
    public void service(@NotNull final Session session, @NotNull final HttpServletResponse resp) {
        final Update update = GsonUtils.gson().fromJson(session.demand(Property.HTTP_BODY_UTF8), Update.class);
        updatesSource.add(update);
    }
}
