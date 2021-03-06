package ru.hypernavi.server.servlet.dump;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


import org.apache.http.entity.ContentType;
import ru.hypernavi.core.session.param.ListQueryParam;
import ru.hypernavi.core.session.Session;

/**
 * User: amosov-f
 * Date: 17.04.16
 * Time: 14:03
 */
public final class DumpWriters {
    public static final ListQueryParam<String> PARAM_DUMP = new ListQueryParam.StringListParam("dump");

    @NotNull
    private final DumpWriter[] dumpWriters = {
            new MainLogDumpWriter()
    };
    @Nullable
    private DumpWriter[] enabledDumpWriters;

    public void enable(@NotNull final Session session, @NotNull final HttpServletRequest req) {
        final List<String> dumpValues = PARAM_DUMP.getValue(req);
        enabledDumpWriters = Arrays.stream(dumpWriters)
                .filter(dw -> dumpValues.contains(dw.getKey()))
                .peek(dw -> dw.enable(session))
                .toArray(DumpWriter[]::new);
    }

    public void dump(@NotNull final Session session, @NotNull final HttpServletResponse resp) throws IOException {
        Objects.requireNonNull(enabledDumpWriters, "Enable DumpWriters before!");
        if (enabledDumpWriters.length != 0 && resp.getContentType() == null) {
            resp.setContentType(ContentType.TEXT_HTML.getMimeType());
        }
        for (final DumpWriter dumpWriter : enabledDumpWriters) {
            dumpWriter.dump(session, resp);
        }
    }
}
