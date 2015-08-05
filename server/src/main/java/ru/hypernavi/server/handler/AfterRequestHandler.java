package ru.hypernavi.server.handler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;


import com.google.common.collect.ImmutableMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.MDC;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.RequestLog;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import ru.hypernavi.server.util.HttpTools;
import ru.hypernavi.util.function.MoreFunctions;

/**
 * User: amosov-f
 * Date: 06.08.15
 * Time: 0:52
 */
public final class AfterRequestHandler extends AbstractLifeCycle implements RequestLog {
    private static final Log ACCESS_LOG = LogFactory.getLog("ACCESS_LOG");

    private static final Map<String, String> ESCAPE_MAP = ImmutableMap.<String, String>builder()
            .put("\"", "%22")
            .put("\n", "%0A")
            .build();

    @NotNull
    private static String n(@Nullable final Object value) {
        return value == null ? "-" : ESCAPE_MAP.entrySet().stream().reduce(
                value.toString(),
                (result, e) -> result.replace(e.getKey(), e.getValue()),
                MoreFunctions.rightProjection()
        );
    }

    @Override
    public void log(@NotNull final Request request, @NotNull final Response response) {
        try {
            final long realTimeStamp = Optional.ofNullable(request.getAttribute(HttpTools.CONTENT_RECEIVED_TS))
                    .map(Long.class::cast)
                    .orElse(request.getTimeStamp());
            final long answerTime = System.currentTimeMillis() - realTimeStamp;
            MDC.put(HttpTools.SERVICE, request.getAttribute(HttpTools.SERVICE));
            MDC.put("reqtimeMs", request.getTimeStamp());
            MDC.put("ip", n(request.getRemoteAddr()));
            MDC.put("anstime", answerTime);
            MDC.put("s", response.getStatus());
            ACCESS_LOG.info("");
        } finally {
            MDC.clear();
        }
    }
}
