package ru.hypernavi.core.database;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by Константин on 24.08.2015.
 */
public interface DataLoader {
    @Nullable
    byte[] load(final String service, final String name);

    @NotNull
    String[] getNames(final String config);

    void save(final String service, final String name, final byte[] data);
}
