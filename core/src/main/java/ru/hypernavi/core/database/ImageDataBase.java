package ru.hypernavi.core.database;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
        nameImage = new HashSet<>();
        this.loader = loader;
        final String[] paths = loader.getPaths();
        for (final String path : paths) {
            LOG.info(path);
            nameImage.add(path);
        }
    }

    @Nullable
    public final byte[] get(@NotNull final String service, @NotNull final String name) {
        return loader.load(service + name);
        /*
        if (nameImage.contains(service + name)) {
            return loader.load(service + name);
        }
        return null;*/
    }

    public void add(@NotNull final String service, @NotNull final String name) {
        /*if ( != null) {
            nameImage.add(service + name);
        }*/
    }

    private final Set<String> nameImage;
}
