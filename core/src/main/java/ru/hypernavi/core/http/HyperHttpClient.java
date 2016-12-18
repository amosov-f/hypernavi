package ru.hypernavi.core.http;

import com.google.inject.Inject;
import net.jcip.annotations.ThreadSafe;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.hypernavi.util.function.IOFunction;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by amosov-f on 04.11.15.
 */
@ThreadSafe
public final class HyperHttpClient implements ru.hypernavi.core.http.HttpClient {
    @NotNull
    private final HttpClient client;

    @Inject
    public HyperHttpClient(@NotNull final HttpClient client) {
        this.client = client;
    }

    @Nullable
    @Override
    public <T> T execute(@NotNull final HttpUriRequest req, @NotNull final IOFunction<InputStream, T> parser) {
        final long start = System.currentTimeMillis();
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
        LOG.debug("Received response in " + (System.currentTimeMillis() - start) + " ms: " + status + " " + statusLine.getReasonPhrase());
        final HttpEntity entity = resp.getEntity();
        if (entity == null) {
            LOG.info("Received empty response in " + (System.currentTimeMillis() - start) + " ms");
            return null;
        }
        try {
            if (status != HttpStatus.SC_OK) {
                LOG.info("Received non-OK response in " + (System.currentTimeMillis() - start) + " ms: " + status + " " + statusLine.getReasonPhrase());
                LOG.debug("Response content: " + IOUtils.toString(entity.getContent()));
                return null;
            }
            try {
                return parser.apply(entity.getContent());
            } catch (@SuppressWarnings("OverlyBroadCatchBlock") RuntimeException e) {
                LOG.error("Received bad response in " + (System.currentTimeMillis() - start) + " ms!", e);
                return null;
            }
        } catch (IOException e) {
            catchIOException(e);
            return null;
        } finally {
            EntityUtils.consumeQuietly(entity);
        }
    }

    private void catchIOException(@NotNull final IOException e) {
        LOG.error("Encountered unrecoverable I/O exception", e);
    }
}
