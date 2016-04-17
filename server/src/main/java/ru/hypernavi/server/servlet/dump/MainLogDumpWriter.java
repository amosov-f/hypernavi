package ru.hypernavi.server.servlet.dump;

import org.jetbrains.annotations.NotNull;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;


import ru.hypernavi.core.session.Session;
import ru.hypernavi.util.log.MemoryAppender;

/**
 * User: amosov-f
 * Date: 17.04.16
 * Time: 13:40
 */
public final class MainLogDumpWriter implements DumpWriter {
    @NotNull
    @Override
    public String getKey() {
        return "main_log";
    }

    @Override
    public void enable(@NotNull final Session session) {
        MemoryAppender.enableForRequest(session.getId());
    }

    @Override
    public void dump(@NotNull final Session session, @NotNull final HttpServletResponse resp) throws IOException {
        final List<String> logs = MemoryAppender.getAndClean(session.getId());
        try {
            final ServletOutputStream out = resp.getOutputStream();
            out.print("<xmp>");
            for (final String s : logs) {
                out.println(s);
            }
            out.print("</xmp>");
        } catch (IllegalStateException ignored) {
            final PrintWriter writer = resp.getWriter();
            writer.print("<xmp>");
            logs.forEach(writer::println);
            writer.print("</xmp>");
        }
    }
}
