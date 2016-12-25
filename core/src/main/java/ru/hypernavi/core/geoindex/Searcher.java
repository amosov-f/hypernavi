package ru.hypernavi.core.geoindex;

import com.google.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.Nullable;
import ru.hypernavi.commons.GeoObject;
import ru.hypernavi.commons.Index;
import ru.hypernavi.commons.SearchResponse;
import ru.hypernavi.commons.Site;
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
    public SearchResponse search(@Nullable final GeoPoint location,
                                 @Nullable final String text,
                                 final int page,
                                 final int numSite)
    {
        GeoPoint searchLocation = location;
        if (location == null) {
            if (text == null) {
                throw new IllegalStateException("Specify text or location!");
            }
            final GeoObject position = mapsSearcher.search(text, null);
            if (position != null) {
                searchLocation = position.getLocation();
            }
        }
        if (searchLocation == null) {
            return null;
        }

        final long start = System.currentTimeMillis();
        LOG.info("Getting NN for location=" + searchLocation + ", page=" + page + ", numSite=" + numSite + " started");
        final List<Index<? extends Site>> sites = geoIndex.getNN(searchLocation, page * numSite, numSite);
        LOG.info("Getting NN finished in " + (System.currentTimeMillis() - start) + " ms, found " + sites.size() + " sites");

        final SearchResponse.Meta meta = new SearchResponse.Meta(searchLocation);
        final SearchResponse.Data data = new SearchResponse.Data(sites);
        return new SearchResponse(meta, data);
    }
}
