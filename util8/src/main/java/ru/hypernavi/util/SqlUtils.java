package ru.hypernavi.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by amosov-f on 28.11.15.
 */
public enum SqlUtils {
    ;

    @NotNull
    public static <T> Stream<T> stream(@NotNull final ResultSet column, @NotNull final Class<T> clazz) throws SQLException {
        final Stream.Builder<T> builder = Stream.builder();
        while (column.next()) {
            builder.accept(column.getObject(1, clazz));
        }
        return builder.build();
    }

    @NotNull
    public static IntStream intStream(@NotNull final ResultSet column) throws SQLException {
        final IntStream.Builder builder = IntStream.builder();
        while (column.next()) {
            builder.accept(column.getInt(1));
        }
        return builder.build();
    }

    @Nullable
    public static Integer integer(@NotNull final ResultSet column) throws SQLException {
        return column.next() ? column.getInt(1) : null;
    }
}
