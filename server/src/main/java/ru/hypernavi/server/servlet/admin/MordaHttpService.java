package ru.hypernavi.server.servlet.admin;

import org.jetbrains.annotations.NotNull;

import javax.servlet.annotation.WebServlet;


import com.google.inject.Inject;
import ru.hypernavi.core.session.RequestReader;
import ru.hypernavi.core.session.Session;
import ru.hypernavi.server.servlet.HtmlPageHttpService;

/**
 * Created by amosov-f on 25.08.15.
 */
@WebServlet(name = "morda", value = "/morda")
public final class MordaHttpService extends HtmlPageHttpService {
    @Inject
    public MordaHttpService(@NotNull final RequestReader.Factory<?> init) {
        super(init);
    }

    @NotNull
    @Override
    public String getPathInBundle(@NotNull final Session session) {
        return "morda.ftl";
    }
}
