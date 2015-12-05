package ru.hypernavi.core.database.provider;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ru.hypernavi.util.GeoPoint;

/**
 * Created by amosov-f on 27.11.15.
 */
public final class GeoPointSqlProvider extends SqlProvider<GeoPoint> {
    private static final Log LOG = LogFactory.getLog(GeoPointSqlProvider.class);

    public GeoPointSqlProvider(@NotNull final Connection conn) {
        super(conn);
    }

    @Nullable
    @Override
    public GeoPoint select(final int id) throws SQLException {
        final ResultSet select = statement.executeQuery("SELECT * FROM geo_point WHERE id = " + id);
        return select.next() ? new GeoPoint(select.getDouble("longitude"), select.getDouble("latitude")) : null;
    }

    @Nullable
    @Override
    public Integer insert(@NotNull final GeoPoint p) throws SQLException {
        final PreparedStatement ps = conn.prepareStatement(
                query("insert", p.getLatitude(), p.getLongitude()),
                Statement.RETURN_GENERATED_KEYS
        );
        ps.executeUpdate();
        final ResultSet rs = ps.getGeneratedKeys();
        return rs.next() ? rs.getInt(1) : null;
    }

    @Nullable
    @Override
    public GeoPoint delete(final int id) throws SQLException {
        final GeoPoint p = select(id);
        if (p == null) {
            return null;
        }
        statement.execute("DELETE FROM geo_point WHERE id = " + id);
        return p;
    }

    @NotNull
    @Override
    protected String query(@NotNull final String name, @NotNull final Object... args) {
        return super.query("/database/geo_point/" + name + ".sql", args);
    }
}
