package ru.hypernavi.core.database;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by Константин on 26.08.2015.
 */
public class ImageResourcesLoader implements DataLoader {
    private static final Log LOG = LogFactory.getLog(ImageResourcesLoader.class);

    private final Set<String> paths = new HashSet<>();

    public ImageResourcesLoader(final String pathDir) {
        loadFromConfig(pathDir);
    }

    @Nullable
    @Override
    public byte[] load(final String path) {
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

    @NotNull
    @Override
    public String[] getPaths() {
        final String[] array = new String[paths.size()];
        return paths.toArray(array);
    }

    @Override
    public void save(final String path, final byte[] data) {
        throw new RuntimeException("Can not save into resources");
    }


    private void loadFromConfig(@NotNull final String pathDir) {
        try {


            LOG.info(pathDir);

            if (ImageResourcesLoader.class.getClassLoader()
                    .getResource(pathDir) == null) {
                LOG.warn("No such directory!");
            }

            if (ImageResourcesLoader.class.getClassLoader()
                    .getResourceAsStream(pathDir) == null) {
                LOG.warn("Fuck there is no files");
            }
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
