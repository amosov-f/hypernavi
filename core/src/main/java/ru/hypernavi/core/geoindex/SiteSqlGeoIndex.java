package ru.hypernavi.core.geoindex;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.util.List;


import com.google.inject.Inject;
import ru.hypernavi.commons.Index;
import ru.hypernavi.commons.Site;
import ru.hypernavi.util.GeoPoint;

/**
 * Created by amosov-f on 28.11.15.
 */
public final class SiteSqlGeoIndex implements GeoIndex<Site> {
    @NotNull
    private final Connection conn;

    @Inject
    public SiteSqlGeoIndex(@NotNull final Connection conn) {
        this.conn = conn;
    }

    @NotNull
    @Override
    public List<Index<? extends Site>> getNN(@NotNull final GeoPoint location, final int offset, final int count) {
        return null;
    }
}
