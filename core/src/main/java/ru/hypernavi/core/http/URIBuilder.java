package ru.hypernavi.core.http;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by amosov-f on 04.11.15.
 */
public final class URIBuilder {
    @NotNull
    private final org.apache.http.client.utils.URIBuilder builder;

    public URIBuilder(@NotNull final String string) {
        try {
            builder = new org.apache.http.client.utils.URIBuilder(string);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public URIBuilder addParameter(@NotNull final String param, @NotNull final String value) {
        builder.addParameter(param, value);
        return this;
    }

    @NotNull
    public URIBuilder addParameterIfNotNull(@NotNull final String param, @Nullable final String value) {
        return value != null ? addParameter(param, value) : this;
    }

    @NotNull
    public URIBuilder addParameter(@NotNull final String param, final int value) {
        return addParameter(param, Integer.toString(value));
    }

    @NotNull
    public URIBuilder addParameter(@NotNull final String param, final double value) {
        return addParameter(param, Double.toString(value));
    }

    @NotNull
    public URIBuilder setParameter(@NotNull final String param, @NotNull final String value) {
        builder.setParameter(param, value);
        return this;
    }

    @NotNull
    public URIBuilder setParameterIfNotNull(@NotNull final String param, @Nullable final String value) {
        return value != null ? setParameter(param, value) : this;
    }

    @NotNull
    public URIBuilder setParameter(@NotNull final String param, final int value) {
        return setParameter(param, Integer.toString(value));
    }

    @NotNull
    public URI build() {
        try {
            return builder.build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public URL buildURL() {
        try {
            return builder.build().toURL();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
