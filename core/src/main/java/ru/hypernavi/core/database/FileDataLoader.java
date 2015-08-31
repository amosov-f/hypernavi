package ru.hypernavi.core.database;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


import com.google.inject.Inject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by Константин on 26.08.2015.
 */
public class FileDataLoader implements DataLoader {
    private static final Log LOG = LogFactory.getLog(FileDataLoader.class);

    private final String dataPath;

    @Inject
    public FileDataLoader(final String dataPath) {
        this.dataPath = dataPath;
    }

    @Nullable
    @Override
    public byte[] load(final String service, final String name) {
        final String path = dataPath + service + name;
        LOG.info("Loading data from path" + path);
        byte[] result = null;
        try {
            final InputStream in = new FileInputStream(new File(path));
            result = IOUtils.toByteArray(in);
        } catch (FileNotFoundException ignored) {
            LOG.warn("No such file");
            return null;
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
        return result;
    }

    @NotNull
    @Override
    public String[] getNames(@NotNull final String service) {
        return getFileFromDirectory(service);
    }

    public String[] getFileFromDirectory(@NotNull final String pathDirectory) {
        LOG.info(dataPath + pathDirectory);
        final List<String> pathsFiles = new ArrayList<>();
        final File folder = new File(dataPath + pathDirectory);

        final File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null) {
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    pathsFiles.add("/"+listOfFiles[i].getName());
                }
            }
            pathsFiles.forEach(LOG::info);
        } else {
            LOG.warn("No files in directory");
        }
        return pathsFiles.toArray(new String[pathsFiles.size()]);
    }

    @Override
    public void save(@NotNull final String service, @NotNull final String name, final byte[] data) {
        final String path = dataPath + service + name;
        try {
            LOG.info("Save file to path: " + path);
            FileUtils.writeByteArrayToFile(new File(path), data);
        } catch (IOException e) {
            LOG.warn("Can not save file\n" + e.getMessage());
        }
    }
}
