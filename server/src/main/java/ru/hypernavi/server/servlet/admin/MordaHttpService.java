package ru.hypernavi.server.servlet.admin;

import org.jetbrains.annotations.NotNull;

import javax.servlet.annotation.WebServlet;


import com.google.inject.Inject;
import freemarker.template.Configuration;
import ru.hypernavi.server.servlet.HtmlPageHttpService;

/**
 * Created by amosov-f on 25.08.15.
 */
@WebServlet(name = "morda", value = "/")
public final class MordaHttpService extends HtmlPageHttpService {
//    @NotNull
//    private final LocalDateTime serverInitTime = LocalDateTime.now(ZoneId.of("Europe/Moscow"));

    @Inject
    public MordaHttpService(@NotNull final Configuration templatesConfig) {
        super(templatesConfig, "morda.ftl");
    }

//    @NotNull
//    @Override
//    public Object getDataModel(@NotNull final HttpServletRequest req) {
//        return ImmutableMap.of("server_init_time", serverInitTime);
//    }
}
