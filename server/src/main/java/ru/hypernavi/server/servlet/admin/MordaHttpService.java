package ru.hypernavi.server.servlet.admin;

import org.jetbrains.annotations.NotNull;

import javax.servlet.annotation.WebServlet;


import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * Created by amosov-f on 25.08.15.
 */
@WebServlet(name = "morda", value = "/")
public final class MordaHttpService extends StaticPageHttpService {
    @Inject
    public MordaHttpService(@Named("hypernavi.web.bundle") @NotNull final String pathToBundle) {
        super(pathToBundle, "morda.html");
    }
}
