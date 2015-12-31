package ru.hypernavi.core.webutil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;


import com.google.inject.Inject;
import org.apache.http.client.methods.HttpGet;
import ru.hypernavi.commons.Dimension;
import ru.hypernavi.core.http.HyperHttpClient;

/**
 * Created by amosov-f on 29.12.15.
 */
public final class ImageDimensioner {
    @NotNull
    private final HyperHttpClient httpClient;

    @Inject
    public ImageDimensioner(@NotNull final HyperHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Nullable
    public Dimension getDimension(@NotNull final String imageLink) {
        final BufferedImage image = httpClient.execute(new HttpGet(imageLink), ImageIO::read);
        if (image == null) {
            return null;
        }
        return Dimension.of(image.getWidth(), image.getHeight());
    }
}
