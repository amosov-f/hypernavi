package ru.hypernavi.core.webutil;

import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import ru.hypernavi.util.awt.ImageUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * User: amosov-f
 * Date: 26.04.16
 * Time: 20:36
 */
public final class ImageEditorTest {
    private static final String DEFAULT_FORMAT = "jpg";

    @Rule
    public final TestWatcher ceanupOnSuccess = new TestWatcher() {
        @Override
        protected void succeeded(@NotNull final Description description) {
            getBufferFile(DEFAULT_FORMAT).delete();
        }
    };

    @Test
    public void createDoubleScaledThumbFromImage() {
        final BufferedImage image = read("/plan-small.jpg");
        final float scale = 2;
        final BufferedImage thumb = ImageEditor.INSTANCE.createThumb(image, scale);
        writeToBufferFile(thumb);
        assertEquals(read("/plan-small-scaled.jpg"), thumb);
    }

    @Test
    public void drawUserLocationOnSmallPlan() {
        final BufferedImage plan = read("/plan-small.jpg");
        final Point location = new Point(300, 250);
        final BufferedImage editedPlan = ImageEditor.INSTANCE.drawLocation(plan, location);
        writeToBufferFile(editedPlan);
        assertEquals(read("/plan-small-location.jpg"), editedPlan);
    }

    @Test
    public void drawUserLocationOnMiddlePlan() {
        final BufferedImage plan = read("/plan-middle.jpg");
        final Point location = new Point(300, 250);
        final BufferedImage editedPlan = ImageEditor.INSTANCE.drawLocation(plan, location);
        writeToBufferFile(editedPlan);
        assertEquals(read("/plan-middle-location.jpg"), editedPlan);
    }

    private void assertEquals(@NotNull final BufferedImage expected, @NotNull final BufferedImage inMemoryActual) {
        writeToBufferFile(inMemoryActual);
        final BufferedImage actual;
        try {
            actual = ImageIO.read(getBufferFile(ImageUtils.format(inMemoryActual, DEFAULT_FORMAT)));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        Assert.assertEquals(expected.getWidth(), actual.getWidth());
        Assert.assertEquals(expected.getHeight(), actual.getHeight());
        for (int x = 0; x < expected.getWidth(); x++) {
            for (int y = 0; y < expected.getHeight(); y++) {
                Assert.assertEquals("(" + x + ", " + y + ")", expected.getRGB(x, y), actual.getRGB(x, y));
            }
        }
    }

    @NotNull
    private BufferedImage read(@NotNull final String classpath) {
        try {
            return ImageIO.read(getClass().getResourceAsStream(classpath));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void writeToBufferFile(@NotNull final BufferedImage image) {
        final String format = ImageUtils.format(image, DEFAULT_FORMAT);
        try {
            ImageIO.write(image, format, getBufferFile(format));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @NotNull
    private File getBufferFile(@NotNull final String format) {
        return new File("buffer." + format);
    }
}