package ru.hypernavi.client.app.util;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import ru.hypernavi.client.app.AppActivity;
import ru.hypernavi.commons.InfoResponceSerializer;
import ru.hypernavi.commons.InfoResponse;

/**
 * Created by Константин on 28.08.2015.
 */
public class CacheWorker {
    private static final Logger LOG = Logger.getLogger(CacheWorker.class.getName());
    private static final String SCHEME_LOCAL_FILE_NAME = "cacheScheme.png";
    private static final String INFO_LOCAL_FILE_NAME = "cacheInfo.txt";
    private static final String SCHEME_PATH = "/file_not_found.jpg";
    private final AppActivity appActivity;

    public CacheWorker(final AppActivity appActivity) {
        this.appActivity = appActivity;
    }

    public void saveSchemeToCache(@NotNull final Bitmap originScheme) {
        final File file = new File(appActivity.getFilesDir(), SCHEME_LOCAL_FILE_NAME);
        LOG.info("Want to cached scheme here: " + file.getAbsolutePath());
        try {
            if (file.createNewFile() || file.exists()) {
                try {
                    final FileOutputStream out = new FileOutputStream(file);
                    LOG.info("file where we write: " + file.toString());
                    originScheme.compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.close();
                    LOG.info("scheme is cached");
                } catch (FileNotFoundException e) {
                    LOG.warning("cached file is null " + e.getMessage());
                } catch (IOException e) {
                    LOG.warning("can't close outputStream " + e.getMessage());
                }
            }
            else {
                LOG.warning("Can't reate file for caching and it not already exists.");
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "can't create file for cached scheme " + e.getMessage());
        }
    }

    // /data/data/ru.hypernavi.client.app/files/cacheInfo.txt

    public void saveInfoResponseToCache(@NotNull final InfoResponse infoResponse) {
        final File file = new File(appActivity.getFilesDir(), INFO_LOCAL_FILE_NAME);
        LOG.info("Want to cached info here: " + file.getAbsolutePath());
        try {
            if (file.createNewFile() || file.exists()) {
                try {
                    final FileOutputStream out = new FileOutputStream(file);
                    LOG.info("file where we write: " + file.toString());
                    out.write(InfoResponceSerializer.serialize(infoResponse).toString().getBytes());
                    out.close();
                    LOG.info("info is cached");
                } catch (FileNotFoundException e) {
                    LOG.warning("cached file is null " + e.getMessage());
                } catch (IOException e) {
                    LOG.warning("can't close outputStream " + e.getMessage());
                }
            }
            else {
                LOG.warning("Can't reate file for caching and it not already exists.");
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "can't create file for cached info " + e.getMessage());
        }
    }

    @NotNull
    public Bitmap loadCachedOrDefaultScheme() {
        final File file = new File(appActivity.getFilesDir(), SCHEME_LOCAL_FILE_NAME);
        LOG.info(file.getAbsolutePath());
        if (file.exists()) {
            try {
                final Bitmap cachedScheme = BitmapFactory.decodeStream(new FileInputStream(file));
                if (cachedScheme == null) {
                    LOG.warning("File: " + file.getPath());
                    LOG.warning("No cached scheme founded in existing file " + file.getPath());
                    //this.deleteFile(file.getPath());
                } else {
                    LOG.info("cached scheme is returned");
                    appActivity.writeWarningMessage("Загружена закэшированная схема");
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
        appActivity.writeWarningMessage("Схем не найдено");
        return defaultScheme;
    }

    public JSONObject loadCachedInfo() {
        final File file = new File(appActivity.getFilesDir(), INFO_LOCAL_FILE_NAME);
        LOG.info(file.getAbsolutePath());
        if (file.exists()) {
            try {
                final FileInputStream input = new FileInputStream(file);
                final byte[] data = IOUtils.toByteArray(input);
                final JSONObject jsonObject = new JSONObject(IOUtils.toString(data, "UTF-8"));
                LOG.info("cached info is returned");
                return jsonObject;
            } catch (FileNotFoundException e) {
                LOG.warning("can't find file " + e.getMessage());
            } catch (IOException e) {
                LOG.warning("can't load from cache" + e);
            } catch (JSONException e) {
                LOG.warning("can't construct JSON" + e);
            }
        }
        LOG.warning("Can't load from cache");
        return null;
    }
}

