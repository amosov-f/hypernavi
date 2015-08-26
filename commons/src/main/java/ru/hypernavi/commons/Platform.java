package ru.hypernavi.commons;

import org.jetbrains.annotations.NotNull;

/**
 * Created by amosov-f on 25.08.15.
 */
public enum Platform {
    DEVELOPMENT, TESTING, PRODUCTION;

    @NotNull
    public static Platform parse(@NotNull final String name) {
        for (final Platform platform : Platform.values()) {
            if (platform.name().equalsIgnoreCase(name)) {
                return platform;
            }
        }
        throw new IllegalArgumentException("Unknown platform: " + name);
    }
}
