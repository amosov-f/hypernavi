package ru.hypernavi.core.database;

import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;


import org.apache.commons.io.IOUtils;
import ru.hypernavi.core.ImageHash;

/**
 * Created by Константин on 24.08.2015.
 */
public interface ImageLoader {
    @Nullable
    byte[] get(final String path);

    @Nullable
    String[] getNames();
}
