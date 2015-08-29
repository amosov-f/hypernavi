package ru.hypernavi.core.database;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.TreeMap;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by Константин on 15.08.2015.
 */
public class ImageDataBase {
    private static final Log LOG = LogFactory.getLog(ImageDataBase.class);
    private final DataLoader loader;

    public ImageDataBase(final DataLoader loader) {
        nameImage = new TreeMap<>();
        this.loader = loader;
        final String[] paths = loader.getPaths();
        if (paths != null) {
            for (final String path : paths) {
                LOG.info(path);
                nameImage.put(path, loader.get(path));
            }
        } else {
            LOG.warn("No files in config");
        }
    }

    @Nullable
    public final byte[] get(@NotNull final String path) {
        if (nameImage.containsKey(path)) {
            return nameImage.get(path);
        }
        return null;
    }

    public void add(@NotNull final String path) {
        nameImage.put(path, loader.get(path));
    }

    private final Map<String, byte[]> nameImage;
}
