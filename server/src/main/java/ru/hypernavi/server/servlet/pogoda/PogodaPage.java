package ru.hypernavi.server.servlet.pogoda;

import org.jetbrains.annotations.NotNull;
import ru.hypernavi.core.session.Session;
import ru.hypernavi.server.servlet.HtmlPageHttpService;

import javax.servlet.annotation.WebServlet;

/**
 * Created by amosov-f on 03.09.16.
 */
@WebServlet(name = "pogoda", value = "/pogoda")
public class PogodaPage extends HtmlPageHttpService {
    @NotNull
    @Override
    public String getPathInBundle(@NotNull final Session session) {
        return "pogoda/pogoda.ftl";
    }
}
