package ru.hypernavi.server.servlet.search;

import org.jetbrains.annotations.NotNull;

import java.util.List;


import com.google.inject.Inject;
import ru.hypernavi.commons.Index;
import ru.hypernavi.commons.SearchResponse;
import ru.hypernavi.commons.Site;
import ru.hypernavi.core.geoindex.GeoIndex;
import ru.hypernavi.core.session.Property;
import ru.hypernavi.core.session.Session;
import ru.hypernavi.util.GeoPoint;

/**
 * Created by amosov-f on 11.12.15.
 */
public final class Searcher {
    @Inject
    private GeoIndex<Site> geoIndex;

    @NotNull
    public SearchResponse search(@NotNull final Session session) {
        final GeoPoint location = session.demand(Property.GEO_LOCATION);
        final int page = session.demand(SearchRequest.PAGE);
        final int numSite = session.demand(SearchRequest.NUM_SITE);

        final List<Index<? extends Site>> sites = geoIndex.getNN(location, page * numSite, numSite);

        final SearchResponse.Meta meta = new SearchResponse.Meta(location);
        final SearchResponse.Data data = new SearchResponse.Data(sites);
        return new SearchResponse(meta, data);
    }
}
