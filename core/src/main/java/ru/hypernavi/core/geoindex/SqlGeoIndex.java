package ru.hypernavi.core.geoindex;

import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;


import ru.hypernavi.commons.Index;
import ru.hypernavi.commons.Positioned;
import ru.hypernavi.util.GeoPoint;

/**
 * Created by amosov-f on 29.11.15.
 */
public interface SqlGeoIndex<T extends Positioned> extends GeoIndex<T> {
    @NotNull
    List<Index<? extends T>> selectNN(@NotNull final GeoPoint location, final int offset, final int count) throws SQLException;

    @NotNull
    @Override
    default List<Index<? extends T>> getNN(@NotNull final GeoPoint location, final int offset, final int count) {
        try {
            return selectNN(location, offset, count);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
