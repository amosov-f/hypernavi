package ru.hypernavi.server.servlet.client;

import org.jetbrains.annotations.NotNull;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


import ru.hypernavi.core.database.AddHypermarkets;
import ru.hypernavi.server.servlet.AbstractHttpService;

/**
 * Created by Константин on 16.09.2015.
 */
@WebServlet(name = "All okey add", value = "/loadOkeys")
public class Runner extends AbstractHttpService {

    @Override
    public void process(@NotNull final HttpServletRequest req, @NotNull final HttpServletResponse resp) throws IOException {
        AddHypermarkets.main();
    }
}
