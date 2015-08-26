package ru.hypernavi.core.database;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;


import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.hypernavi.core.ImageHash;

/**
 * Created by Константин on 15.08.2015.
 */
public class ImageDataBase<T extends ImageLoader> {
    private static final Log LOG = LogFactory.getLog(ImageDataBase.class);
    private final T loader;

    public ImageDataBase(final T loader) {
        nameImage = new TreeMap<>();
        this.loader = loader;
        final String[] paths = loader.getNames();
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
