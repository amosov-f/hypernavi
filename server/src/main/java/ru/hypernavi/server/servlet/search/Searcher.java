package ru.hypernavi.server.servlet.search;

import com.google.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.hypernavi.commons.GeoObject;
import ru.hypernavi.commons.Index;
import ru.hypernavi.commons.SearchResponse;
import ru.hypernavi.commons.Site;
import ru.hypernavi.core.geoindex.GeoIndex;
import ru.hypernavi.core.session.Property;
import ru.hypernavi.core.session.Session;
import ru.hypernavi.core.webutil.yandex.MapsSearcher;
import ru.hypernavi.util.GeoPoint;

import java.util.List;

/**
 * Created by amosov-f on 11.12.15.
 */
public final class Searcher {
    private static final Log LOG = LogFactory.getLog(Searcher.class);

    @Inject
    private GeoIndex<Site> geoIndex;
    @Inject
    private MapsSearcher mapsSearcher;

    @Nullable
    public SearchResponse search(@NotNull final Session session) {
        GeoPoint location = session.get(Property.GEO_LOCATION);
        if (location == null) {
            final String text = session.demand(Property.TEXT);
            final GeoObject position = mapsSearcher.search(text, null);
            if (position != null) {
                location = position.getLocation();
            }
        }
        if (location == null) {
            return null;
        }
        final int page = session.demand(SearchRequest.PAGE);
        final int numSite = session.demand(SearchRequest.NUM_SITE);

        final long start = System.currentTimeMillis();
        final List<Index<? extends Site>> sites = geoIndex.getNN(location, page * numSite, numSite);
        LOG.info("Getting NN finished in " + (System.currentTimeMillis() - start) + " ms");

        final SearchResponse.Meta meta = new SearchResponse.Meta(location);
        final SearchResponse.Data data = new SearchResponse.Data(sites);
        return new SearchResponse(meta, data);
    }
}
