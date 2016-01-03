package ru.hypernavi.server.servlet.admin;

import org.jetbrains.annotations.NotNull;

import javax.servlet.annotation.WebServlet;
import java.time.LocalDateTime;
import java.time.ZoneId;


import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import ru.hypernavi.core.auth.AdminRequestReader;
import ru.hypernavi.core.database.HypermarketHolder;
import ru.hypernavi.core.session.RequestReader;
import ru.hypernavi.core.session.Session;
import ru.hypernavi.server.servlet.HtmlPageHttpService;

/**
 * Created by Константин on 02.09.2015.
 */
@WebServlet(name = "adminka", value = "/admin")
public final class AdminService extends HtmlPageHttpService {
    private final HypermarketHolder markets;
    @NotNull
    private final LocalDateTime initTime = LocalDateTime.now(ZoneId.of("Europe/Moscow"));

    @Inject
    public AdminService(@NotNull final HypermarketHolder markets, @NotNull final RequestReader.Factory<AdminRequestReader> reader) {
        super("admin.ftl", reader);
        this.markets = markets;
    }

    @NotNull
    @Override
    public Object toDataModel(@NotNull final Session session) {
        final ImmutableMap.Builder<String, Object> dataModel = new ImmutableMap.Builder<>();
        dataModel.put("number", markets.size());
        dataModel.put("server_starts", initTime);
        dataModel.put("hypermarkets", markets.getAll());
        return dataModel.build();
    }
}
