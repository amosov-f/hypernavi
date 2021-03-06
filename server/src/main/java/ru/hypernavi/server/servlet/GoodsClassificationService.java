package ru.hypernavi.server.servlet;

import org.jetbrains.annotations.NotNull;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


import com.google.common.net.MediaType;
import com.google.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ru.hypernavi.core.Category;
import ru.hypernavi.core.classify.goods.GoodsClassifier;
import ru.hypernavi.core.session.Property;
import ru.hypernavi.core.session.Session;

/**
 * User: amosov-f
 * Date: 24.11.14
 * Time: 0:35
 */
@WebServlet(name = "category", value = "/category")
public final class GoodsClassificationService extends AbstractHttpService {
    private static final Log LOG = LogFactory.getLog(GoodsClassificationService.class);

    @NotNull
    private final GoodsClassifier classifier;

    @Inject
    public GoodsClassificationService(@NotNull final GoodsClassifier classifier) {
        this.classifier = classifier;
    }

    @Override
    public void service(@NotNull final Session session, @NotNull final HttpServletResponse resp) throws IOException {
        final String text = session.get(Property.TEXT);

        // business logic
        final Category category = text != null ? classifier.classify(text) : null;
        if (category != null) {
            LOG.info("'" + text + "' classified as '" + category.getName() + "'");
        }

        // response writing
        resp.setStatus(category != null ? HttpServletResponse.SC_OK : HttpServletResponse.SC_BAD_REQUEST);
        resp.setContentType(MediaType.PLAIN_TEXT_UTF_8.type());
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resp.getWriter().println(category != null ? category.getName() : "No text in query!");
    }
}
