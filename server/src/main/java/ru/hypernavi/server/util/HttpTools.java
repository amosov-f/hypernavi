package ru.hypernavi.server.util;

import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

/**
 * User: amosov-f
 * Date: 06.08.15
 * Time: 0:34
 */
public enum HttpTools {
    ;

    public static final String CONTENT_RECEIVED_TS = "requestFullyReceivedTS";
    public static final String SERVICE = "service";

    @NotNull
    public static String requestURL(@NotNull final HttpServletRequest request) {
        final StringBuffer requestURL = request.getRequestURL();
        if (request.getQueryString() != null) {
            requestURL.append("?").append(request.getQueryString());
        }
        return requestURL.toString();
    }

    @NotNull
    public static String curl(@NotNull final HttpServletRequest req) {
        final StringBuilder sb = new StringBuilder("curl \"").append(requestURL(req)).append("\" -i ");
        for (final String headerName : Collections.list(req.getHeaderNames())) {
            for (final String headerValue : Collections.list(req.getHeaders(headerName))) {
                sb.append("-H \"").append(headerName).append(":").append(headerValue).append("\" ");
            }
        }
        return sb.append("| less").toString();
    }
}
