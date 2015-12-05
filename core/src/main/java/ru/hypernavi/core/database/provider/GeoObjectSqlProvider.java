package ru.hypernavi.core.database.provider;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;


import com.google.inject.Inject;
import ru.hypernavi.commons.GeoObject;
import ru.hypernavi.util.GeoPoint;

/**
 * Created by amosov-f on 28.11.15.
 */
public class GeoObjectSqlProvider extends SqlProvider<GeoObject> {
    @NotNull
    private final DatabaseProvider<GeoPoint> geoPointProvider;

    @Inject
    public GeoObjectSqlProvider(@NotNull final Connection conn, @NotNull final DatabaseProvider<GeoPoint> geoPointProvider) {
        super(conn);
        this.geoPointProvider = geoPointProvider;
    }

    @Nullable
    @Override
    public GeoObject select(final int id) throws SQLException {
        final ResultSet select = statement.executeQuery("SELECT * FROM geo_object WHERE id = " + id);
        if (!select.next()) {
            return null;
        }
        final String name = select.getString("name");
        final String description = select.getString("description");
        final int geoPointId = select.getInt("geo_point_id");
        final GeoPoint geoPoint = geoPointProvider.get(geoPointId);
        return geoPoint != null ? new GeoObject(name, description, geoPoint) : null;
    }

    @Nullable
    @Override
    public Integer insert(@NotNull final GeoObject geoObject) throws SQLException {
        final Integer geoPointId = geoPointProvider.add(geoObject.getLocation());
        if (geoPointId == null) {
            return null;
        }
        final PreparedStatement ps = conn.prepareStatement(
                query("insert", geoObject.getName(), geoObject.getDescription(), geoPointId),
                Statement.RETURN_GENERATED_KEYS
        );
        ps.executeUpdate();
        final ResultSet rs = ps.getGeneratedKeys();
        return rs.next() ? rs.getInt(1) : null;
    }

    @Nullable
    @Override
    public GeoObject delete(final int id) throws SQLException {
        final GeoObject geoObject = select(id);
        if (geoObject == null) {
            return null;
        }
        conn.createStatement().execute(query("delete", id));
        return geoObject;
    }

    @NotNull
    @Override
    protected String query(@NotNull final String name, @NotNull final Object... args) {
        return super.query("/database/geo_object/" + name + ".sql", args);
    }
}
