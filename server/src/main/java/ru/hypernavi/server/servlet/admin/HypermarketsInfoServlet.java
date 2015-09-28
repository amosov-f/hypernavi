package ru.hypernavi.server.servlet.admin;

import org.jetbrains.annotations.NotNull;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;


import java.time.LocalDateTime;
import java.time.ZoneId;


import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import freemarker.template.Configuration;
import ru.hypernavi.core.database.HypermarketHolder;
import ru.hypernavi.server.servlet.HtmlPageHttpService;

/**
 * Created by Константин on 02.09.2015.
 */
@WebServlet(name = "info about hypermarkets", value = "/admin")
public class HypermarketsInfoServlet extends HtmlPageHttpService {
    private final HypermarketHolder markets;
    @NotNull
    private final LocalDateTime initTime = LocalDateTime.now(ZoneId.of("Europe/Moscow"));


    @Inject
    public HypermarketsInfoServlet(@NotNull final HypermarketHolder markets, @NotNull final Configuration templatesConfig) {
        super(templatesConfig, "hypermarket_list.ftl");
        this.markets = markets;
    }

    @NotNull
    @Override
    public Object getDataModel(@NotNull final HttpServletRequest req) {
        final ImmutableMap.Builder<String, Object> dataModel = new ImmutableMap.Builder<>();
        dataModel.put("number", markets.size());
        dataModel.put("server_starts", initTime);
        dataModel.put("hypermarkets", markets.getAll());
        return dataModel.build();
    }
}
