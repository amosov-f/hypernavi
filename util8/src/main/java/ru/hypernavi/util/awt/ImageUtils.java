package ru.hypernavi.util.awt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.hypernavi.util.stream.MoreStreamSupport;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * User: amosov-f
 * Date: 26.04.16
 * Time: 22:55
 */
public enum ImageUtils {
    ;

    private static final Log LOG = LogFactory.getLog(ImageUtils.class);

    private static final String[] FORMATS = {"jpg", "png", "gif"};

    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(10);

    @NotNull
    public static String format(@Nullable final BufferedImage image, @Nullable final String imageLink) {
        final String imageLinkFormat = Optional.ofNullable(imageLink)
                .map(ImageUtils::format)
                .orElse("jpg");
        return Optional.ofNullable(image)
                .map(ImageUtils::format)
                .orElse(imageLinkFormat);
    }

    @Nullable
    public static String format(@NotNull final String imageLink) {
        for (final String format : FORMATS) {
            if (imageLink.endsWith(format)) {
                return format;
            }
        }
        for (final String format : FORMATS) {
            if (imageLink.toLowerCase().contains("." + format)) {
                return format;
            }
        }
        return null;
    }

    @Nullable
    public static String format(@NotNull final BufferedImage image) {
        return MoreStreamSupport.stream(ImageIO.getImageReaders(image)).map(ImageUtils::format).findFirst().orElse(null);
    }

    @NotNull
    private static String format(@NotNull final ImageReader reader) {
        try {
            return reader.getFormatName();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @NotNull
    public static BufferedImage copy(@NotNull final BufferedImage image) {
        final long start = System.currentTimeMillis();
        final BufferedImage copy = image.getSubimage(0, 0, image.getWidth(), image.getHeight());
        LOG.info("Copying image " + image.getWidth() + "x" + image.getHeight() + " finished in " + (System.currentTimeMillis() - start));
        return copy;
    }

    public static void drawCircle(@NotNull final Graphics2D g, @NotNull final Point p, final int r) {
        g.drawOval(p.x - r, p.y - r, 2 * r, 2 * r);
    }

    public static void fillCircle(@NotNull final Graphics2D g, @NotNull final Point p, final int r) {
        g.fillOval(p.x - r, p.y - r, 2 * r, 2 * r);
    }

    @NotNull
    public static byte[] toByteArray(@NotNull final BufferedImage image) {
        final ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, format(image, "jpg"), bout);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bout.toByteArray();
    }

    @NotNull
    public static byte[] toByteArray(@NotNull final BufferedImage image, @NotNull final String format) {
        final ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, format, bout);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bout.toByteArray();
    }

    @NotNull
    public static Future<BufferedImage> downloadAsync(@NotNull final String link) {
        return EXECUTOR.submit(() -> download(link));
    }

    @NotNull
    public static BufferedImage download(@NotNull final String link) throws IOException {
        final long start = System.currentTimeMillis();
        LOG.debug("Started downloading: " + link);
        try {
            return ImageIO.read(new URL(link));
        } catch (IOException e) {
            LOG.error("Can't download image: " + link, e);
            throw e;
        } finally {
            LOG.debug("Image downloading finished in " + (System.currentTimeMillis() - start) + " ms");
        }
    }

    @Nullable
    public static BufferedImage downloadSafe(@NotNull final String link) {
        try {
            return download(link);
        } catch (IOException ignored) {
            return null;
        }
    }
}
