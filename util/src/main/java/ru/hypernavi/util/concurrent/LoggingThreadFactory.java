package ru.hypernavi.util.concurrent;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;


import net.jcip.annotations.ThreadSafe;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by amosov-f on 04.11.15.
 */
@ThreadSafe
public final class LoggingThreadFactory implements ThreadFactory {
    private static final Log LOG = LogFactory.getLog(LoggingThreadFactory.class);

    @NotNull
    private final String name;
    private final boolean daemon;
    @NotNull
    private final AtomicInteger threadNumber = new AtomicInteger(1);

    public LoggingThreadFactory(@NotNull final String name) {
        this(name, true);
    }

    public LoggingThreadFactory(@NotNull final String name, final boolean daemon) {
        this.name = name;
        this.daemon = daemon;
    }

    @NotNull
    @Override
    public Thread newThread(@NotNull final Runnable r) {
        final SecurityManager manager = System.getSecurityManager();
        final ThreadGroup group = manager != null ? manager.getThreadGroup() : Thread.currentThread().getThreadGroup();
        final Thread t = new Thread(group, r, name + "-" + threadNumber.getAndIncrement(), 0);
        t.setDaemon(daemon);
        t.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(@NotNull final Thread thread, @NotNull final Throwable ex) {
                LOG.error("Thread " + thread + " dead", ex);
            }
        });
        return t;
    }
}
