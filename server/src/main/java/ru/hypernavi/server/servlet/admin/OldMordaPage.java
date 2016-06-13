package ru.hypernavi.server.servlet.admin;

import org.jetbrains.annotations.NotNull;
import ru.hypernavi.core.session.Session;
import ru.hypernavi.server.servlet.HtmlPageHttpService;

import javax.servlet.annotation.WebServlet;

/**
 * Created by amosov-f on 25.08.15.
 */
@WebServlet(name = "old morda", value = {"/morda"})
public final class OldMordaPage extends HtmlPageHttpService {
    @NotNull
    @Override
    public String getPathInBundle(@NotNull final Session session) {
        return "old-morda.ftl";
    }
}
