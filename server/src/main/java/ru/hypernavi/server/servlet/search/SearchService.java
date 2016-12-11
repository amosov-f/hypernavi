package ru.hypernavi.server.servlet.search;

import com.google.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import ru.hypernavi.commons.SearchResponse;
import ru.hypernavi.core.geoindex.Searcher;
import ru.hypernavi.core.session.Property;
import ru.hypernavi.core.session.Session;
import ru.hypernavi.core.session.SessionInitializer;
import ru.hypernavi.server.servlet.AbstractHttpService;
import ru.hypernavi.util.GeoPoint;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
        final GeoPoint location = session.get(Property.GEO_LOCATION);
        final String text = session.get(Property.TEXT);
        final int page = session.demand(SearchRequest.PAGE);
        final int numSite = session.demand(SearchRequest.NUM_SITE);
        final SearchResponse searchResponse = searcher.search(location, text, page, numSite);
        if (searchResponse == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No location in request!");
            return;
        }
        write(searchResponse, resp);
    }
}
