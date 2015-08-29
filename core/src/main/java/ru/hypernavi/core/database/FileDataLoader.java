package ru.hypernavi.core.database;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by Константин on 26.08.2015.
 */
public class FileDataLoader implements DataLoader {
    private static final Log LOG = LogFactory.getLog(FileDataLoader.class);
    private final String[] paths;


    public FileDataLoader(final String pathDir) {

        final List<String> images = new ArrayList<>();
        final File folder = new File(pathDir);

        final File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null) {
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    images.add(pathDir + listOfFiles[i].getName());
                }
            }
            images.forEach(LOG::info);
        } else {
            LOG.warn("No files in directory");
        }

        paths = images.toArray(new String[images.size()]);
    }

    @Nullable
    @Override
    public byte[] get(final String name) {
        LOG.info(name);
        byte[] result = null;
        try {
            final InputStream in = new FileInputStream(new File(name));
            result = IOUtils.toByteArray(in);
        } catch (IOException ignored) {
            LOG.error("No such file");
        }
        return result;
    }

    @Nullable
    @Override
    public String[] getPaths() {
        return paths;
    }
}
