package ru.hypernavi.core.database;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;


import org.apache.commons.io.IOUtils;
import ru.hypernavi.core.ImageHash;

/**
 * Created by Константин on 24.08.2015.
 */
public interface DataLoader {
    @Nullable
    byte[] load(final String path);

    @NotNull
    String[] getPaths();

    void save(final String path, final byte[] data);
}
