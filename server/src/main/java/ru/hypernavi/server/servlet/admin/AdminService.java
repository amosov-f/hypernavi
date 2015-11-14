package ru.hypernavi.server.servlet.admin;

import org.jetbrains.annotations.NotNull;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;


import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import freemarker.template.Configuration;
import ru.hypernavi.core.auth.AdminRequestReader;
import ru.hypernavi.core.auth.VkAuthValidator;
import ru.hypernavi.core.database.HypermarketHolder;
import ru.hypernavi.core.session.Session;
import ru.hypernavi.core.session.SessionInitializer;
import ru.hypernavi.server.servlet.HtmlPageHttpService;

/**
 * Created by Константин on 02.09.2015.
 */
@WebServlet(name = "adminka", value = "/admin")
public final class AdminService extends HtmlPageHttpService {
    private final HypermarketHolder markets;
    @NotNull
    private final LocalDateTime initTime = LocalDateTime.now(ZoneId.of("Europe/Moscow"));
    @NotNull
    private final VkAuthValidator authValidator;

    @Inject
    public AdminService(@NotNull final HypermarketHolder markets,
                        @NotNull final Configuration templatesConfig,
                        @NotNull final VkAuthValidator authValidator)
    {
        super(templatesConfig, "admin.ftl");
        this.markets = markets;
        this.authValidator = authValidator;
    }

    @NotNull
    @Override
    public SessionInitializer getInitializer(@NotNull final HttpServletRequest req) {
        return new AdminRequestReader(req, authValidator);
    }

    @NotNull
    @Override
    public Object getDataModel(@NotNull final Session session) {
        final ImmutableMap.Builder<String, Object> dataModel = new ImmutableMap.Builder<>();
        dataModel.put("number", markets.size());
        dataModel.put("server_starts", initTime);
        dataModel.put("hypermarkets", markets.getAll());
        return dataModel.build();
    }
}
