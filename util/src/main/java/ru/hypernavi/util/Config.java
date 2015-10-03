package ru.hypernavi.util;

import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by amosov-f on 22.08.15.
 */
public final class Config extends Properties {
    private static final Log LOG = LogFactory.getLog(Config.class);

    @NotNull
    public static Config load(@NotNull final String... paths) throws IOException {
        final Config config = new Config();
        for (final String path : paths) {
            try (InputStream in = MoreIOUtils.getInputStream(path)) {
                if (in != null) {
                    LOG.info("Loading config properties from '" + path + "'");
                    config.load(in);
                } else {
                    LOG.info("Skipped config properties from '" + path + "', not exists");
                }
            } catch (FileNotFoundException ignored) {
                LOG.info("Skipped config local properties from '" + path + "', not exists");
            }
        }
        return config;
    }

    @NotNull
    public static Config of(@NotNull final String... keyValues) {
        final Config config = new Config();
        for (int i = 0; i < keyValues.length - 1; i += 2) {
            config.setProperty(keyValues[i], keyValues[i + 1]);
        }
        return config;
    }

    @NotNull
    public Config subConfig(@NotNull final String node) {
        final Config config = new Config();
        for (final String key : stringPropertyNames()) {
            if (key.startsWith(node + ".")) {
                config.setProperty(key.substring(node.length() + 1), getProperty(key));
            }
        }
        return config;
    }

    public int getInt(@NotNull final String key) {
        return Integer.parseInt(getProperty(key));
    }
}
