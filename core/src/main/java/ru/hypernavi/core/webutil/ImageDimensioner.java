package ru.hypernavi.core.webutil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


import com.google.inject.Inject;
import org.apache.http.client.methods.HttpGet;
import ru.hypernavi.commons.Dimension;
import ru.hypernavi.core.http.HttpClient;

/**
 * Created by amosov-f on 29.12.15.
 */
public final class ImageDimensioner {
    @NotNull
    private final HttpClient httpClient;
    @NotNull
    private final Map<String, Dimension> cache = new ConcurrentHashMap<>();

    @Inject
    public ImageDimensioner(@NotNull final HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Nullable
    public Dimension getDimension(@NotNull final String imageLink) {
        return cache.computeIfAbsent(imageLink, this::getDimensionImpl);
    }

    @Nullable
    public Dimension getDimension(@NotNull final BufferedImage image) {
        return Dimension.of(image.getWidth(), image.getHeight());
    }

    @Nullable
    public Dimension getDimensionImpl(@NotNull final String imageLink) {
        return Optional.ofNullable(httpClient.execute(new HttpGet(imageLink), ImageIO::read)).map(this::getDimension).orElse(null);
    }
}
