package ru.hypernavi.core.database;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Statement;

/**
 * Created by amosov-f on 04.10.15.
 */
public final class SQLDataLoader implements DataLoader {
    @NotNull
    private final Statement statement;

    public SQLDataLoader(@NotNull final Statement statement) {
        this.statement = statement;
    }

    @Nullable
    @Override
    public byte[] load(@NotNull final String service, @NotNull final String name) {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public String[] getNames(@NotNull final String config) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void save(@NotNull final String service, @NotNull final String name, @NotNull final byte[] data) {
        throw new UnsupportedOperationException();
    }
}
