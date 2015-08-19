package ru.hypernavi.server.servlet.admin;

import com.google.common.net.MediaType;
import org.apache.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import ru.hypernavi.server.servlet.AbstractHttpService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/**
 * Created by amosov-f on 19.08.15.
 */
@WebServlet(name = "server_info", value = "/admin/info")
public final class ServerInfoHttpService extends AbstractHttpService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.MEDIUM);

    @NotNull
    private final LocalDateTime initTime = LocalDateTime.now(ZoneId.of("Europe/Moscow"));

    @Override
    public void process(@NotNull final HttpServletRequest req,
                        @NotNull final HttpServletResponse resp) throws IOException
    {
        resp.setStatus(HttpStatus.SC_OK);
        resp.setContentType(MediaType.PLAIN_TEXT_UTF_8.type());
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.getWriter().println("Server initialization time:\t" + initTime.format(FORMATTER));
    }
}
