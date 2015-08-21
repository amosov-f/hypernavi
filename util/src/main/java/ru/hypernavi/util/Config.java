package ru.hypernavi.util;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by amosov-f on 22.08.15.
 */
public final class Config extends Properties {
    @NotNull
    public static Config fromJar(@NotNull final String... paths) throws IOException {
        final Config config = new Config();
        for (final String path : paths) {
            config.load(Config.class.getResourceAsStream(path));
        }
        return config;
    }
}
