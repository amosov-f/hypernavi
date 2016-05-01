package ru.hypernavi.core.http;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


import org.apache.http.client.methods.HttpUriRequest;
import ru.hypernavi.util.function.IOFunction;

/**
 * User: amosov-f
 * Date: 01.05.16
 * Time: 23:39
 */
public final class MockHttpClient implements HttpClient {
    @NotNull
    private final Map<URI, byte[]> database = new ConcurrentHashMap<>();

    public void registry(@NotNull final HttpUriRequest req, @NotNull final byte[] resp) {
        database.put(req.getURI(), resp);
    }

    @Nullable
    @Override
    public <T> T execute(@NotNull final HttpUriRequest req, @NotNull final IOFunction<InputStream, T> parser) {
        final byte[] resp = database.get(req.getURI());
        if (resp == null) {
            return null;
        }
        try {
            return parser.apply(new ByteArrayInputStream(resp));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}