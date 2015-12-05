package ru.hypernavi.core.geoindex;

import org.jetbrains.annotations.NotNull;

import java.util.List;


import com.google.inject.Inject;
import ru.hypernavi.commons.Site;
import ru.hypernavi.core.database.provider.mongo.SiteMongoProvider;
import ru.hypernavi.util.GeoPoint;

/**
 * Created by amosov-f on 05.12.15.
 */
public final class SiteMongoGeoIndex implements GeoIndex<Site> {
    @NotNull
    private final SiteMongoProvider provider;

    @Inject
    public SiteMongoGeoIndex(@NotNull final SiteMongoProvider provider) {
        this.provider = provider;
    }

    @NotNull
    @Override
    public List<Site> getNN(@NotNull final GeoPoint location, final int offset, final int count) {
        return provider.getNN(location, offset, count);
    }
}
