package ru.hypernavi.core.database;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;


import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by Константин on 26.08.2015.
 */
public class ImageResourcesLoader implements ImageLoader {
    private static final Log LOG = LogFactory.getLog(ImageResourcesLoader.class);

    private final Set<String> paths = new HashSet<>();

    public ImageResourcesLoader(final String pathDir) {
        loadFromConfig(pathDir);
    }

    @Nullable
    @Override
    public byte[] get(final String path) {
        if (paths.contains(path)) {
            try {
                LOG.info(path);
                return IOUtils.toByteArray(getClass().getResourceAsStream(path));
            } catch (IOException ignored) {
                return null;
            }
        }
        return null;
    }

    @Nullable
    @Override
    public String[] getNames() {
        final String[] array = new String[paths.size()];
        return paths.toArray(array);
    }


    private void loadFromConfig(@NotNull final String pathDir) {
        try {
            final List<String> localpaths = IOUtils.readLines(
                    ImageResourcesLoader.class.getClassLoader()
                            .getResourceAsStream(pathDir), Charsets.UTF_8);
            LOG.info(localpaths.size());
            localpaths.forEach(LOG::info);
            //localpaths.stream().map(s -> "/" + pathDir + s);

            //final Stream<String> listPath = IOUtils.readLines(
            //        ImageResourcesLoader.class.getClassLoader()
            //                .getResourceAsStream(pathDir), Charsets.UTF_8)
            //        .stream().map(s -> "/" + pathDir + s);


            Collections.addAll(paths, localpaths.stream().map(s -> "/" + pathDir + s).toArray(String[]::new));

            paths.forEach(LOG::info);
        } catch (IOException e) {
            LOG.warn(e.getMessage());
        }
    }
}
