package ru.hypernavi.util.log;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;


import com.google.common.base.Joiner;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.MDC;
import org.apache.log4j.spi.LoggingEvent;

/**
 * User: amosov-f
 * Date: 17.04.16
 * Time: 12:52
 */
public final class MemoryAppender extends AppenderSkeleton {
    private static final LoadingCache<String, List<String>> logs = CacheBuilder.newBuilder()
            .expireAfterAccess(1, TimeUnit.MINUTES)
            .maximumSize(10)
            .build(new CacheLoader<String, List<String>>() {
                @NotNull
                @Override
                public List<String> load(@NotNull final String key) throws Exception {
                    return Collections.synchronizedList(new ArrayList<String>());
                }
            });

    public static void enableForRequest(@NotNull final String reqid) {
        logs.getUnchecked(reqid);
    }

    @NotNull
    public static List<String> getAndClean(@NotNull final String reqId) {
        final List<String> target = logs.getIfPresent(reqId);
        if (target == null) {
            return Collections.emptyList();
        }
        logs.invalidate(reqId);
        return target;
    }

    @Override
    protected void append(@NotNull final LoggingEvent event) {
        final String reqId = (String) MDC.get("reqid");
        if (reqId == null) {
            return;
        }

        final List<String> target = logs.getIfPresent(reqId);
        if (target == null) {
            return;
        }

        target.add(layout.format(event));

        if (layout.ignoresThrowable()) {
            final String[] throwableStrRep = event.getThrowableStrRep();
            if (throwableStrRep != null) {
                target.add(Joiner.on("\n").join(throwableStrRep));
            }
        }
    }

    @Override
    public void close() {
        logs.invalidateAll();
    }

    @Override
    public boolean requiresLayout() {
        return true;
    }
}
