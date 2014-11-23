package ru.navi.server.servlet;

import com.google.inject.Inject;
import org.jetbrains.annotations.NotNull;
import ru.navi.hyper.algo.GoodsClassifier;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * User: amosov-f
 * Date: 24.11.14
 * Time: 0:35
 */
public class HyperNaviServlet extends HttpServlet {
    @NotNull
    private final GoodsClassifier classifier;

    @Inject
    public HyperNaviServlet(@NotNull GoodsClassifier classifier) {
        this.classifier = classifier;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final String text = req.getParameter("text");
        resp.getWriter().println(classifier.classify(text));
    }
}
