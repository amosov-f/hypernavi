package ru.hypernavi.ml.factor;

import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.io.File;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by Константин on 09.09.2015.
 */
public class CachedFactor<T extends CacheableObject> extends Factor<T> {
    private static final Log LOG = LogFactory.getLog(CachedFactor.class);

    public CachedFactor(final Factor<T> factor) {
        super("cached_" + factor.getName());
        path = "data/cache_feature/" + factor.getName() + ".txt";
        cache = new HashMap<>();
        loadCache(path);
        this.factor = factor;
    }

    private final String path;
    private final Map<String, Double> cache;
    private final Factor<T> factor;

    @Override
    public double applyAsDouble(@NotNull final T value) {
        if (!cache.containsKey(value.hash())) {
            final double result = factor.applyAsDouble(value);
            cache.put(value.hash(), result);
            save(value.hash(), result);
        }
        return cache.get(value.hash());
    }


    private void loadCache(@NotNull final String path) {
        LOG.info("Loading cache...");
        try {
            final InputStream in = new FileInputStream(path);
            final List<String> lines = IOUtils.readLines(in);
            for (final String line : lines) {
                final String[] parts = line.split("\t");
                if (parts.length < 2) {
                    continue;
                }
                cache.put(parts[0], Double.parseDouble(parts[1]));
            }

        } catch (IOException e) {
            LOG.warn(e.getMessage());
            LOG.info("Cache not loaded.");
            return;
        }
        LOG.info("Cache loaded!");
        LOG.info("Amount elements added " + cache.size());
    }

    private void save(@NotNull final String value, final double result) {
        try {
            final File file = new File(path);
            if (!file.exists()) {
                Files.write(Paths.get(path), (value + "\t" + result + "\n").getBytes(), StandardOpenOption.CREATE_NEW);
            } else {
                Files.write(Paths.get(path), (value + "\t" + result + "\n").getBytes(), StandardOpenOption.APPEND);
            }
        } catch (IOException e) {
            LOG.warn(e.getMessage());
            LOG.info("Info not added");
        }
    }
}
