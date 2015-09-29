package ru.hypernavi.core.webutil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;


import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Константин on 28.09.2015.
 */
public final class GeocoderSender {

    @NotNull
    private static final HttpClient client = HttpClientBuilder.create()
            .disableContentCompression()
            .setMaxConnPerRoute(100)
            .setMaxConnTotal(100)
            .build();

    private GeocoderSender() {
    }

    @Nullable
    public static JSONObject getGeocoderResponse(@NotNull final String address) {
        final HttpResponse geocode;
        try {
            final URI path = new URIBuilder()
                    .setScheme("https")
                    .setHost("geocode-maps.yandex.ru")
                    .setPath("/1.x")
                    .addParameter("geocode", address)
                    .addParameter("format", "json")
                    .build();
            geocode = client.execute(new HttpGet(path));
        } catch (IOException | URISyntaxException ignored) {
            return null;
        }

        if (geocode == null) {
            return null;
        }

        final String geocodeResponse;
        try {
            geocodeResponse = IOUtils.toString(geocode.getEntity().getContent(), StandardCharsets.UTF_8.name());
        } catch (IOException ignored) {
            return null;
        }
        if (geocodeResponse == null) {
            return null;
        }

        try {
            return new JSONObject(geocodeResponse);
        } catch (JSONException ignored) {
            return null;
        }
    }
}
