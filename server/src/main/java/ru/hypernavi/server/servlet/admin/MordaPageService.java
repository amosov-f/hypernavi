package ru.hypernavi.server.servlet.admin;

import org.jetbrains.annotations.NotNull;
import ru.hypernavi.core.session.Session;
import ru.hypernavi.server.servlet.HtmlPageHttpService;

import javax.servlet.annotation.WebServlet;

/**
 * Created by amosov-f on 19.11.16
 */
@WebServlet(name = "morda", value = {"", "/"})
public class MordaPageService extends HtmlPageHttpService {
  @NotNull
  @Override
  public String getPathInBundle(@NotNull final Session session) {
    return "morda.ftl";
  }
}
