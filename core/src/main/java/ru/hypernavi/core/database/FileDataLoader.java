package ru.hypernavi.core.database;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by Константин on 26.08.2015.
 */
public class FileDataLoader implements DataLoader {
    private static final Log LOG = LogFactory.getLog(FileDataLoader.class);

    private final String pathDir;

    public FileDataLoader(final String pathDir) {
        this.pathDir = pathDir;
    }

    @Nullable
    @Override
    public byte[] load(final String name) {
        LOG.info(name);
        byte[] result = null;
        try {
            final InputStream in = new FileInputStream(new File(name));
            result = IOUtils.toByteArray(in);
        } catch (IOException ignored) {
            // TODO amosov-f: toByteArray IOExeption -> error, new FileInputStream FileNotFoundException -> return null
            LOG.warn("No such file");
        }
        return result;
    }

    @NotNull
    @Override
    public String[] getPaths() {
        final List<String> pathsFiles = new ArrayList<>();
        final File folder = new File(pathDir);

        final File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null) {
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    pathsFiles.add(pathDir + listOfFiles[i].getName());
                }
            }
            pathsFiles.forEach(LOG::info);
        } else {
            LOG.warn("No files in directory");
        }

        return pathsFiles.toArray(new String[pathsFiles.size()]);
    }

    @Override
    public void save(final String path, final byte[] data) {
        try {
            FileUtils.writeByteArrayToFile(new File(path), data);
        } catch (IOException e) {
            LOG.warn("Can not save file\n" + e.getMessage());
        }
    }
}
