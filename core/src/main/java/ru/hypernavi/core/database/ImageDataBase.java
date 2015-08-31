package ru.hypernavi.core.database;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by Константин on 15.08.2015.
 */
public class ImageDataBase {
    private static final Log LOG = LogFactory.getLog(ImageDataBase.class);
    private final DataLoader loader;

    @Inject
    public ImageDataBase(@NotNull final FileDataLoader loader, @Named("hypernavi.server.serviceimg") final String service) {
        this.loader = loader;
        final String[] paths = loader.getNames(service);
        for (final String path : paths) {
            LOG.info(path);
        }
    }

    @Nullable
    public final byte[] get(@NotNull final String service, @NotNull final String name) {
        return loader.load(service, name);
     }

    public void add(@NotNull final String service, @NotNull final String name, final byte[] data) {
        loader.save(service, name, data);
    }
}
