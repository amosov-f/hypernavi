package ru.hypernavi.server.acceptance;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;


import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import ru.hypernavi.server.HyperNaviServerLauncher;

/**
 * Created by amosov-f on 22.08.15.
 */
public class AcceptanceTest {
    private static final HttpHost LOCALHOST = new HttpHost("localhost", HyperNaviServerLauncher.PORT);

    @NotNull
    private final HttpClient client = HttpClientBuilder.create()
            .disableContentCompression()
            .setMaxConnPerRoute(100)
            .setMaxConnTotal(100)
            .build();

    @NotNull
    protected HttpResponse execute(@NotNull final String uri) {
        try {
            return client.execute(LOCALHOST, new HttpGet(uri));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
