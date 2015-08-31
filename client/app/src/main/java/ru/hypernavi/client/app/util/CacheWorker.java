package ru.hypernavi.client.app.util;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import ru.hypernavi.client.app.AppActivity;

/**
 * Created by Константин on 28.08.2015.
 */
public class CacheWorker {
    private static final Logger LOG = Logger.getLogger(CacheWorker.class.getName());
    private static final String LOCAL_FILE_NAME = "cacheScheme.png";
    private static final String SCHEME_PATH = "/file_not_found.jpg";
    private final AppActivity appActivity;

    public CacheWorker(final AppActivity appActivity) {
        this.appActivity = appActivity;
    }

    public void saveSchemeToCache(final Bitmap originScheme) {
        final File file = new File(appActivity.getFilesDir(), LOCAL_FILE_NAME);
        try {
            if (file.createNewFile()) {
                try {
                    final FileOutputStream out = new FileOutputStream(file);
                    LOG.warning("file where we write: " + file.toString());
                    originScheme.compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.close();
                    LOG.info("scheme is cached");
                } catch (FileNotFoundException e) {
                    LOG.warning("cached file is null " + e.getMessage());
                } catch (IOException e) {
                    LOG.warning("can't close outputStream " + e.getMessage());
                }
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "can't create file for cached scheme " + e.getMessage());
        }
    }

    @NotNull
    public Bitmap loadCachedOrDefaultScheme() {
        final File file = new File(appActivity.getFilesDir(), LOCAL_FILE_NAME);
        LOG.warning(file.getAbsolutePath());
        if (file.exists()) {
            try {
                final Bitmap cachedScheme = BitmapFactory.decodeStream(new FileInputStream(file));
                if (cachedScheme == null) {
                    LOG.warning("File: " + file.getPath());
                    LOG.warning("No cached scheme founded in existing file " + file.getPath());
                    //this.deleteFile(file.getPath());
                } else {
                    LOG.info("cached scheme is returned");
                    appActivity.writeWarningMessage("Cached map is loaded!");
                    return cachedScheme;
                }
            } catch (FileNotFoundException e) {
                LOG.warning("can't find file " + e.getMessage());
            }
        }

        final Bitmap defaultScheme = BitmapFactory.decodeStream(getClass().getResourceAsStream(SCHEME_PATH));
        if (defaultScheme == null) {
            throw new RuntimeException("No default scheme founded");
        }
        LOG.info("default scheme is returned");
        appActivity.writeWarningMessage("Default image is loaded!");
        return defaultScheme;
    }
}

