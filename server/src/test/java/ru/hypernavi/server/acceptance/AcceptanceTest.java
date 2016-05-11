package ru.hypernavi.server.acceptance;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.Objects;


import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import ru.hypernavi.server.HandleErrorsInLog;
import ru.hypernavi.server.HyperNaviServerLauncher;

/**
 * Created by amosov-f on 22.08.15.
 */
public class AcceptanceTest {
    private static final HttpHost LOCALHOST = new HttpHost("localhost", HyperNaviServerLauncher.PORT);

    @Rule
    public final TestRule handleErrorsInLog = new HandleErrorsInLog();
    @Rule
    public final TestRule timeout = new DisableOnDebug(Timeout.seconds(5));

    @NotNull
    private final HttpClient client = HttpClientBuilder.create()
            .disableContentCompression()
            .setMaxConnPerRoute(100)
            .setMaxConnTotal(100)
            .disableRedirectHandling()
            .build();

    @NotNull
    protected final HttpResponse execute(@NotNull final String uri) {
        return execute(new HttpGet(uri));
    }

    @NotNull
    protected final HttpResponse execute(@NotNull final HttpUriRequest req) {
        try {
            return client.execute(LOCALHOST, req);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    protected void assertJsonEquals(@NotNull final String classpathToExpected, @NotNull final String actual) {
        final JsonElement expectedJson = fromClasspath(classpathToExpected);
        final JsonElement actualJson =  new JsonParser().parse(actual);
        if (Objects.equals(expectedJson, actualJson)) {
            return;
        }
        Assert.assertEquals(expectedJson, actualJson);
    }

    @NotNull
    protected JsonElement fromClasspath(@NotNull final String classpath) {
        return new JsonParser().parse(new InputStreamReader(getClass().getResourceAsStream(classpath)));
    }
}
