package ru.hypernavi.util.concurrent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.*;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by amosov-f on 04.11.15.
 */
final class LoggingScheduledThreadPoolExecutor extends ScheduledThreadPoolExecutor {
    private static final Log LOG = LogFactory.getLog(LoggingScheduledThreadPoolExecutor.class);

    @NotNull
    private final String name;

    LoggingScheduledThreadPoolExecutor(final int corePoolSize, @NotNull final String name, final boolean daemon) {
        super(corePoolSize, new LoggingThreadFactory(name, daemon));
        this.name = name;
    }

    @Override
    protected void afterExecute(@NotNull final Runnable r, @Nullable final Throwable t) {
        Throwable e = t;
        if (e == null && r instanceof Future<?> && ((Future<?>) r).isDone()) {
            try {
                ((Future<?>) r).get();
            } catch (CancellationException ce) {
                e = ce;
            } catch (ExecutionException ee) {
                e = ee.getCause();
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }
        if (e != null) {
            LOG.error("Exception in " + name + " thread", e);
        }
    }
}
