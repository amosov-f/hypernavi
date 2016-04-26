package ru.hypernavi.core.http;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;


import com.google.gson.Gson;
import com.google.inject.Inject;
import net.jcip.annotations.ThreadSafe;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;
import ru.hypernavi.util.TextUtils;
import ru.hypernavi.util.function.IOFunction;
import ru.hypernavi.util.json.MoreGsonUtils;

/**
 * Created by amosov-f on 04.11.15.
 */
@ThreadSafe
public final class HyperHttpClient {
    private static final Log LOG = LogFactory.getLog(HyperHttpClient.class);

    @NotNull
    private final HttpClient client;

    @Inject
    public HyperHttpClient(@NotNull final HttpClient client) {
        this.client = client;
    }

    @Nullable
    public <T> T execute(@NotNull final HttpUriRequest req, @NotNull final IOFunction<InputStream, T> parser) {
        LOG.debug("Requesting data from: " + HttpTools.curl(req));
        final HttpResponse resp;
        try {
            resp = client.execute(req);
        } catch (IOException e) {
            catchIOException(e);
            return null;
        }
        final StatusLine statusLine = resp.getStatusLine();
        final int status = statusLine.getStatusCode();
        LOG.debug("Received response: " + status + " " + statusLine.getReasonPhrase());
        final HttpEntity entity = resp.getEntity();
        if (entity == null) {
            LOG.info("Received empty response");
            return null;
        }
        try {
            if (status != HttpStatus.SC_OK) {
                LOG.info("Received non-OK response: " + status + " " + statusLine.getReasonPhrase());
                return null;
            }
            try {
                return parser.apply(entity.getContent());
            } catch (@SuppressWarnings("OverlyBroadCatchBlock") RuntimeException e) {
                LOG.error("Received bad response!", e);
                return null;
            }
        } catch (IOException e) {
            catchIOException(e);
            return null;
        } finally {
            EntityUtils.consumeQuietly(entity);
        }
    }

    @Nullable
    public <T> T executeBytes(@NotNull final HttpUriRequest req, @NotNull final IOFunction<byte[], T> parser) {
        return execute(req, in -> parser.apply(IOUtils.toByteArray(in)));
    }

    @Nullable
    public <T> T executeText(@NotNull final HttpUriRequest req, @NotNull final IOFunction<String, T> parser) {
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
    public <T> T execute(@NotNull final HttpUriRequest req, @NotNull final Class<T> clazz, @NotNull final Gson gson) {
        return executeText(req, MoreGsonUtils.parser(gson, clazz));
    }

    private void catchIOException(@NotNull final IOException e) {
        LOG.error("Encountered unrecoverable I/O exception", e);
    }
}
