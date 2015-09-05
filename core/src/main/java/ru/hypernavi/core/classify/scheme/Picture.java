package ru.hypernavi.core.classify.scheme;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;


import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import ru.hypernavi.commons.Chain;
import ru.hypernavi.util.MD5;
import ru.hypernavi.util.MoreIOUtils;

/**
 * Created by amosov-f on 03.09.15.
 */
public final class Picture {
    private static final Log LOG = LogFactory.getLog(Picture.class);

    @NotNull
    private final URL url;
    @NotNull
    private final MBFImage image;
    @Nullable
    private Chain chain;

    public Picture(@NotNull final URL url, @NotNull final MBFImage image) {
        this.url = url;
        this.image = image;
    }

    @NotNull
    public URL getUrl() {
        return url;
    }

    @NotNull
    public MBFImage getImage() {
        return image;
    }

    @Nullable
    public Chain getChain() {
        return chain;
    }

    public void setChain(@Nullable final Chain chain) {
        this.chain = chain;
    }

    @Nullable
    public static Picture download(@NotNull final String url, @Nullable final Chain chain) {
        final Picture picture = download(url);
        if (picture == null) {
            return null;
        }
        picture.setChain(chain);
        return picture;
    }

    @Nullable
    public static Picture download(@NotNull final String url) {
        final byte[] imageBytes = getOrDownload(url);
        if (imageBytes == null) {
            return null;
        }
        try {
            final Picture picture = new Picture(new URL(url), ImageUtilities.readMBF(new ByteArrayInputStream(imageBytes)));
            LOG.info("Image loaded: " + url);
            return picture;
        } catch (MalformedURLException e) {
            // catches in getOrDownload
            throw new RuntimeException(e);
        } catch (IOException e) {
            LOG.error("Can't read MBF image!", e);
            return null;
        }
    }

    @Nullable
    private static byte[] getOrDownload(@NotNull final String url) {
        try {
            return FileUtils.readFileToByteArray(cacheFile(url));
        } catch (FileNotFoundException ignored) {
            LOG.info("Downloading image: " + url);
            final byte[] data;
            try {
                data = MoreIOUtils.read(url);
            } catch (IOException e) {
                LOG.error("No image by url: " + url, e);
                return null;
            }
            try {
                FileUtils.writeByteArrayToFile(cacheFile(url), data);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return data;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private static File cacheFile(@NotNull final String url) {
        return new File("data/cache", MD5.generate(url.getBytes(StandardCharsets.UTF_8)));
    }
}
