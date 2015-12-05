package ru.hypernavi.core.geoindex;

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;


import com.google.inject.Inject;
import ru.hypernavi.commons.Site;
import ru.hypernavi.core.database.provider.DatabaseProvider;
import ru.hypernavi.util.GeoPoint;
import ru.hypernavi.util.SqlUtils;

/**
 * Created by amosov-f on 29.11.15.
 */
public final class DummySiteSqlGeoIndex implements SqlGeoIndex<Site> {
    @NotNull
    private final PreparedStatement statement;
    @NotNull
    private final DatabaseProvider<Site> siteProvider;

    @Inject
    public DummySiteSqlGeoIndex(@NotNull final Connection conn, @NotNull final DatabaseProvider<Site> siteProvider) {
        try {
            this.statement = conn.prepareStatement("SELECT id FROM site");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        this.siteProvider = siteProvider;
    }

    @NotNull
    @Override
    public List<Site> selectNN(@NotNull final GeoPoint location, final int offset, final int count) throws SQLException {
        final String[] ids = SqlUtils.intStream(statement.executeQuery()).mapToObj(String::valueOf).toArray(String[]::new);
        final Site[] sites = siteProvider.get(ids).toArray(Site[]::new);
        return new DummyGeoIndex<>(sites).getNN(location, offset, count);
    }
}
