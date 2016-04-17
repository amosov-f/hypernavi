package ru.hypernavi.server.servlet.dump;

import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletResponse;


import java.io.IOException;


import ru.hypernavi.core.session.Session;

/**
 * User: amosov-f
 * Date: 17.04.16
 * Time: 13:38
 */
public interface DumpWriter {
    @NotNull
    String getKey();

    void enable(@NotNull Session session);

    void dump(@NotNull Session session, @NotNull HttpServletResponse resp) throws IOException;
}
