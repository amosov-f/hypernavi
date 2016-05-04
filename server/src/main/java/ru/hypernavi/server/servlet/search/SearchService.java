package ru.hypernavi.server.servlet.search;

import org.jetbrains.annotations.NotNull;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


import com.google.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ru.hypernavi.commons.SearchResponse;
import ru.hypernavi.core.session.Session;
import ru.hypernavi.core.session.SessionInitializer;
import ru.hypernavi.server.servlet.AbstractHttpService;

/**
 * Created by amosov-f on 11.12.15.
 */
@WebServlet(name = "search", value = "/search")
public final class SearchService extends AbstractHttpService {
    private static final Log LOG = LogFactory.getLog(SearchService.class);

    @Inject
    private Searcher searcher;

    @NotNull
    @Override
    protected SessionInitializer createReader(@NotNull final HttpServletRequest req) {
        return new SearchRequest(req);
    }

    @Override
    public void service(@NotNull final Session session, @NotNull final HttpServletResponse resp) throws IOException {
        final SearchResponse searchResponse = searcher.search(session);
        if (searchResponse == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No location in request!");
            return;
        }
        write(searchResponse, resp);
    }
}
