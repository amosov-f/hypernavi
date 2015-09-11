package ru.hypernavi.core.classify.scheme;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import ru.hypernavi.commons.Chain;
import ru.hypernavi.ml.HugeObject;
import ru.hypernavi.ml.factor.CacheableObject;
import ru.hypernavi.util.MD5;
import ru.hypernavi.util.MoreIOUtils;

/**
 * Created by amosov-f on 03.09.15.
 */
public final class Picture implements HugeObject, CacheableObject {
    private static final Log LOG = LogFactory.getLog(Picture.class);

    @NotNull
    private final URL url;
    @Nullable
    private MBFImage image;
    @Nullable
    private Chain chain;

    private Picture(@NotNull final URL url) {
        this.url = url;
    }

    @NotNull
    public URL getUrl() {
        return url;
    }

    @NotNull
    public MBFImage getImage() {
        return Objects.requireNonNull(image);
    }

    @Nullable
    public Chain getChain() {
        return chain;
    }

    @Override
    public void fill() {
        try {
            image = cachedImage(url.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void empty() {
        image = null;
    }

    private void setChain(@Nullable final Chain chain) {
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
        if (!cache(url)) {
            return null;
        }
        final Picture picture;
        try {
            picture = new Picture(new URL(url));
        } catch (MalformedURLException e) {
            // catches in cache()
            throw new RuntimeException(e);
        }
        // checking image loading
//        try {
//            cachedImage(url);
//        } catch (IOException e) {
//            LOG.error("Can't read MBF image!", e);
//            return null;
//        }
        LOG.info("Picture loaded: " + url);
        return picture;
    }

    @NotNull
    public static Picture[] download() {
        try {
            return IOUtils.readLines(MoreIOUtils.getInputStream("classpath:/dataset/chains.txt")).stream()
                    .map(line -> line.split("\t"))
                    .map(parts -> download(parts[1], Chain.parse(parts[0])))
                    .filter(Objects::nonNull)
                    .toArray(Picture[]::new);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull static Picture[] downloadOkeyDataSet() {
        try {
            return IOUtils.readLines(MoreIOUtils.getInputStream("classpath:/dataset/urls.txt")).stream()
                .map(line -> line.split("\t"))
                .map(parts -> download(parts[1], Chain.parse(parts[0])))
                .filter(Objects::nonNull)
                .toArray(Picture[]::new);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean cache(@NotNull final String url) {
        if (cacheFile(url).exists()) {
            return true;
        }
        LOG.info("Downloading image: " + url);
        final byte[] data;
        try {
            data = MoreIOUtils.read(url);
        } catch (IOException e) {
            LOG.error("No image by url: " + url, e);
            return false;
        }
        try {
            FileUtils.writeByteArrayToFile(cacheFile(url), data);
            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private static MBFImage cachedImage(@NotNull final String url) throws IOException {
        return ImageUtilities.readMBF(cacheFile(url));
    }

    @NotNull
    private static File cacheFile(@NotNull final String url) {
        return new File("data/cache", MD5.generate(url.getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    @NotNull
    public String toString() {
        return url.toString();
    }

    @NotNull
    @Override
    public String hash() {
        return url.toString();
    }
}
