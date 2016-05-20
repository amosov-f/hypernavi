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
    private org.apache.http.client.utils.URIBuilder builder;

    public URIBuilder(@NotNull final String string) {
        builder = create(string);
    }

    @NotNull
    public URIBuilder add(@NotNull final String param, @NotNull final String value) {
        builder.addParameter(param, value);
        return this;
    }

    @NotNull
    public URIBuilder addIfNotNull(@NotNull final String param, @Nullable final String value) {
        return value != null ? add(param, value) : this;
    }

    @NotNull
    public URIBuilder add(@NotNull final String param, final int value) {
        return add(param, Integer.toString(value));
    }

    @NotNull
    public URIBuilder add(@NotNull final String param, final double value) {
        return add(param, Double.toString(value));
    }

    @NotNull
    public URIBuilder addIfNotNull(@NotNull final String param, @Nullable final Double value) {
        return value != null ? add(param, value) : this;
    }

    @NotNull
    public URIBuilder set(@NotNull final String param, @NotNull final String value) {
        builder.setParameter(param, value);
        return this;
    }

    @NotNull
    public URIBuilder setIfNotNull(@NotNull final String param, @Nullable final String value) {
        return value != null ? set(param, value) : this;
    }

    @NotNull
    public URIBuilder set(@NotNull final String param, final int value) {
        return set(param, Integer.toString(value));
    }

    @NotNull
    public URIBuilder remove(@NotNull final String name) {
        builder = create(builder.toString().replaceAll("[&?]" + name + ".*?(?=&|\\?|$)", ""));
        return this;
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

    @NotNull
    private org.apache.http.client.utils.URIBuilder create(@NotNull final String string) {
        try {
            return new org.apache.http.client.utils.URIBuilder(string);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
