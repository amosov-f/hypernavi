package ru.hypernavi.core.http;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

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
    public static Map<String, String> params(@NotNull final HttpUriRequest req) {
        return URLEncodedUtils.parse(req.getURI(), StandardCharsets.UTF_8.name()).stream()
            .collect(Collectors.toMap(NameValuePair::getName, NameValuePair::getValue));
    }

    @NotNull
    public static String curl(@NotNull final HttpUriRequest req) {
        return curl(req.getURI().toString(), content(req), headers(req));
    }

    @NotNull
    public static String curl(@NotNull final HttpServletRequest req) {
        return curl(requestURL(req), null, headers(req));
    }

    @NotNull
    private static String curl(@NotNull final String url,
                               @Nullable final String data,
                               @NotNull final List<Pair<String, String>> headers)
    {
        final StringBuilder sb = new StringBuilder("curl \"").append(url).append("\" -i ");
        if (data != null) {
            sb.append(" -d \'").append(data).append("\' ");
        }
        for (final Pair<String, String> header : headers) {
            sb.append("-H \"").append(header.getKey()).append(":").append(header.getValue()).append("\" ");
        }
        return sb.append("| less").toString();
    }

    @Nullable
    public static String content(@NotNull final HttpUriRequest req) {
        if (req instanceof HttpPost) {
            final HttpEntity entity = ((HttpPost) req).getEntity();
            if (entity instanceof StringEntity) {
                try {
                    return IOUtils.toString(entity.getContent());
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }
        return null;
    }
}
