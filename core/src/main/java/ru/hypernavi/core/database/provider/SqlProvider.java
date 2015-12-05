package ru.hypernavi.core.database.provider;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


import org.apache.commons.io.IOUtils;

/**
 * Created by amosov-f on 27.11.15.
 */
public abstract class SqlProvider<T> implements DatabaseProvider<T> {
    private final Map<String, String> queries = new HashMap<>();

    @NotNull
    protected final Connection conn;
    @NotNull
    protected final Statement statement;

    protected SqlProvider(@NotNull final Connection conn) {
        this.conn = conn;
        try {
            statement = conn.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    public abstract T select(final int id) throws SQLException;

    @Nullable
    public abstract Integer insert(@NotNull final T obj) throws SQLException;

    @Nullable
    public abstract T delete(final int id) throws SQLException;

    @Nullable
    @Override
    public T get(final int id) {
        try {
            return select(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    @Override
    public Integer add(@NotNull final T obj) {
        try {
            return insert(obj);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    @Override
    public T remove(final int id) {
        try {
            return delete(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    protected String query(@NotNull final String path, @NotNull final Object... args) {
        if (!queries.containsKey(path)) {
            try {
                queries.put(path, IOUtils.toString(SqlProvider.class.getResourceAsStream(path), StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return String.format(Locale.US, queries.get(path), args);
    }
}
