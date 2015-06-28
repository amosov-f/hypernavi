package ru.hypernavi.server.servlet;

import com.google.common.net.MediaType;
import com.google.inject.Inject;
import org.jetbrains.annotations.NotNull;
import ru.hypernavi.core.classify.GoodsClassifier;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * User: amosov-f
 * Date: 24.11.14
 * Time: 0:35
 */
public final class GoodsClassificationService extends HttpServlet {
    @NotNull
    private final GoodsClassifier classifier;

    @Inject
    public GoodsClassificationService(@NotNull final GoodsClassifier classifier) {
        this.classifier = classifier;
    }

    @Override
    protected void doGet(@NotNull final HttpServletRequest req,
                         @NotNull final HttpServletResponse resp) throws ServletException, IOException
    {
        // request reading
        final String text = req.getParameter("text");

        // response writing
        resp.setContentType(MediaType.PLAIN_TEXT_UTF_8.type());
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.getWriter().println(text != null ? classifier.classify(text).getName() : "No text in query!");
    }
}
