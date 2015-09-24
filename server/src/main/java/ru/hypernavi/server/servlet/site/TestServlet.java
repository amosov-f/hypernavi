package ru.hypernavi.server.servlet.site;

import org.jetbrains.annotations.NotNull;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;


import com.google.common.net.MediaType;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import ru.hypernavi.server.servlet.AbstractHttpService;

/**
 * Created by Константин on 23.09.2015.
 */
@WebServlet(name = "Test", value = "/testRussian")
public class TestServlet extends AbstractHttpService {
    private static final Logger LOG = Logger.getLogger(TestServlet.class.getName());

    @Override
    public final void process(@NotNull final HttpServletRequest req, @NotNull final HttpServletResponse resp) throws IOException {
        resp.setStatus(HttpStatus.SC_OK);
        resp.setContentType(MediaType.ANY_TEXT_TYPE.type());
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        final String example = "Русский язык.\n";
        LOG.info(example);
        final OutputStream out = resp.getOutputStream();
        out.write(example.getBytes(StandardCharsets.UTF_8.name()));
        out.write(example.getBytes());
        IOUtils.write(example.getBytes(StandardCharsets.UTF_8.name()), out);
        IOUtils.write(example, out, StandardCharsets.UTF_8.name());
        IOUtils.write(example, out);

    }

}
