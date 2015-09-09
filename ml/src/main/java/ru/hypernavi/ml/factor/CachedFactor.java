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

/**
 * Created by Константин on 09.09.2015.
 */
public abstract class CachedFactor<T> extends Factor<T> {
    protected CachedFactor(@NotNull final String name, boolean usingCache) {
        super(name);
        path = "data/cache_feature/" + name + ".txt";
        cache = new HashMap<>();
        lazy = usingCache;
        loadCache(path);
    }

    private final String path;
    private final Map<String, Double> cache;
    private final boolean lazy;

    @Override
    public double applyAsDouble(@NotNull final T value) {
        if (lazy) {
            if (!cache.containsKey(value.toString())) {
                final double result = applyCachedDouble(value);
                cache.put(value.toString(), result);
                save(value.toString(), result);
            }
            return cache.get(value.toString());
        }
        return applyCachedDouble(value);
    }

    public abstract double applyCachedDouble(@NotNull final T value);

    private void loadCache(@NotNull final String path) {
        System.out.println("Loading cache...");
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

        } catch (IOException ignored) {
            System.out.println("FUCK" + ignored.getMessage());
        }
        System.out.println("Cache loaded!\nAmount elements added " + cache.size());
    }

    private void save(@NotNull final String value, final double result) {
        try {
            final File file = new File(path);
            if (!file.exists()) {
                Files.write(Paths.get(path), (value + "\t" + result + "\n").getBytes(), StandardOpenOption.CREATE_NEW);
            } else {
                Files.write(Paths.get(path), (value + "\t" + result + "\n").getBytes(), StandardOpenOption.APPEND);
            }
        } catch (IOException ignored) {
            System.out.println("FUCK2 " + ignored.getMessage());
        }
    }
}
