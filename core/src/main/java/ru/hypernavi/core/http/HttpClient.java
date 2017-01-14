package ru.hypernavi.core.http;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.HttpUriRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.hypernavi.util.TextUtils;
import ru.hypernavi.util.function.IOFunction;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * User: amosov-f
 * Date: 01.05.16
 * Time: 23:04
 */
public interface HttpClient {
    Log LOG = LogFactory.getLog(HttpClient.class);
    @NotNull
    ThreadLocal<JsonElement> JSON_RESPONSE = new ThreadLocal<>();

    @Nullable
    <T> T execute(@NotNull HttpUriRequest req, @NotNull IOFunction<InputStream, T> parser);

    @Nullable
    default <T> T executeBytes(@NotNull final HttpUriRequest req, @NotNull final IOFunction<byte[], T> parser) {
        return execute(req, in -> parser.apply(IOUtils.toByteArray(in)));
    }

    @Nullable
    default <T> T executeText(@NotNull final HttpUriRequest req, @NotNull final IOFunction<String, T> parser) {
        return execute(req, in -> {
            final String content = IOUtils.toString(in, StandardCharsets.UTF_8);
            try {
                return parser.apply(content);
            } catch (RuntimeException e) {
                LOG.error("Received bad response: " + TextUtils.limit(content, 1000), e);
                return null;
            }
        });
    }

    @Nullable
    default <T> T execute(@NotNull final HttpUriRequest req, @NotNull final Class<T> clazz, @NotNull final Gson gson) {
        return executeText(req, s -> {
            final JsonElement json = gson.fromJson(s, JsonElement.class);
            JSON_RESPONSE.set(json);
            return gson.fromJson(json, clazz);
        });
    }
}
