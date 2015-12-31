package ru.hypernavi.core.server;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * Created by amosov-f on 25.08.15.
 */
public enum Platform {
    DEVELOPMENT, TEST, TESTING, PRODUCTION;

    public boolean isLocal() {
        return this == DEVELOPMENT || this == TEST;
    }

    public boolean isRemote() {
        return this == TESTING || this == PRODUCTION;
    }

    @NotNull
    public static Platform parse(@NotNull final String name) {
        return Arrays.stream(Platform.values())
                .filter(platform -> platform.name().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown platform: " + name));
    }
}
