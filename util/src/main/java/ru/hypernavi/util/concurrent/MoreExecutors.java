package ru.hypernavi.util.concurrent;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by amosov-f on 04.11.15.
 */
public enum MoreExecutors {
    ;

    @NotNull
    public static ScheduledThreadPoolExecutor newSingleThreadScheduledExecutor(@NotNull final String name) {
        return newSingleThreadScheduledExecutor(name, true);
    }

    @NotNull
    public static ScheduledThreadPoolExecutor newSingleThreadScheduledExecutor(@NotNull final String name, final boolean daemon) {
        return new LoggingScheduledThreadPoolExecutor(1, name, daemon);
    }
}
