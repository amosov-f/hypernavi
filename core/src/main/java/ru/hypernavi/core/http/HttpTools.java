package ru.hypernavi.core.http;

import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;


import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.client.methods.HttpUriRequest;

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
    public static String requestURL(@NotNull final HttpServletRequest req) {
        final StringBuffer requestURL = req.getRequestURL();
        if (req.getQueryString() != null) {
            requestURL.append("?").append(req.getQueryString());
        }
        return requestURL.toString();
    }

    @NotNull
    public static String requestURI(@NotNull final HttpServletRequest req) {
        final String requestURI = req.getRequestURI();
        return req.getQueryString() != null ? requestURI + "?" + req.getQueryString() : requestURI;
    }

    @NotNull
    public static List<Pair<String, String>> headers(@NotNull final HttpUriRequest req) {
        return Arrays.stream(req.getAllHeaders()).map(header -> Pair.of(header.getName(), header.getValue())).collect(Collectors.toList());
    }

    @NotNull
    public static List<Pair<String, String>> headers(@NotNull final HttpServletRequest httpServletRequest) {
        final List<Pair<String, String>> headers = new ArrayList<>();
        for (final String name : Collections.list(httpServletRequest.getHeaderNames())) {
            for (final String value : Collections.list(httpServletRequest.getHeaders(name))) {
                headers.add(Pair.of(name, value));
            }
        }
        return headers;
    }

    @NotNull
    public static String curl(@NotNull final HttpUriRequest req) {
        return curl(req.getURI().toString(), headers(req));
    }

    @NotNull
    public static String curl(@NotNull final HttpServletRequest req) {
        return curl(requestURL(req), headers(req));
    }

    @NotNull
    private static String curl(@NotNull final String url, @NotNull final List<Pair<String, String>> headers) {
        final StringBuilder sb = new StringBuilder("curl \"").append(url).append("\" -i ");
        for (final Pair<String, String> header : headers) {
            sb.append("-H \"").append(header.getKey()).append(":").append(header.getValue()).append("\" ");
        }
        return sb.append("| less").toString();
    }
}
