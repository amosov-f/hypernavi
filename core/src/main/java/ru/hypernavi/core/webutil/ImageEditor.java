package ru.hypernavi.core.webutil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import ru.hypernavi.core.telegram.LocationImage;
import ru.hypernavi.util.awt.ImageUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;


/**
 * User: amosov-f
 * Date: 26.04.16
 * Time: 20:33
 */
public enum ImageEditor {
    INSTANCE;

    private static final Log LOG = LogFactory.getLog(ImageEditor.class);

    private static final double MIN_X_SCALE = 0.2;
    private static final double MIN_Y_SCALE = 0.2;

    @NotNull
    private final BufferedImage locationPicture;
    private final Point locationTipShift = new Point(4, 2);

    ImageEditor() {
        try {
            locationPicture = ImageIO.read(getClass().getResourceAsStream("/bot/location.png"));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @NotNull
    public BufferedImage createThumb(@NotNull final BufferedImage image, final int height) {
        final float scale = 1.0f * image.getHeight() / height;
        return createThumb(image, scale);
    }

    @NotNull
    public BufferedImage createThumb(@NotNull final BufferedImage image, final float scale) {
        final int w = (int) (image.getWidth() / scale);
        final int h = (int) (image.getHeight() / scale);
        final BufferedImage thumb = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        final Image scaledImage = image.getScaledInstance(w, h, BufferedImage.SCALE_SMOOTH);
        thumb.createGraphics().drawImage(scaledImage, 0, 0, null);
        return thumb;
    }

    @NotNull
    public BufferedImage drawLocation(@NotNull final BufferedImage plan, @NotNull final Point location) {
        if (LocationImage.checkLocationInsideImage(plan, location)) {
            return drawLocationPicture(plan, location);
        }
        return plan;
    }

    @NotNull
    public BufferedImage drawLocationCircle(@NotNull final BufferedImage plan, @NotNull final Point location) {
        final int w = plan.getWidth();
        final int h = plan.getHeight();
        final int l = Math.max(w, h);
        final BufferedImage editedPlan = ImageUtils.copy(plan);
        final Graphics2D g = editedPlan.createGraphics();
        g.setStroke(new BasicStroke(Math.max(l / 125, 1)));
        g.setColor(Color.RED);
        final int r1 = Math.max(l / 20, 1);
        ImageUtils.drawCircle(g, location, r1);
        final int r2 = Math.max(l  / 150, 1);
        ImageUtils.fillCircle(g, location, r2);
        return plan;
    }

    @NotNull
    public BufferedImage drawLocationPicture(@NotNull final BufferedImage plan, @NotNull final Point location) {
        final long start = System.currentTimeMillis();

        final double scaleX = MIN_X_SCALE * plan.getWidth() / locationPicture.getWidth();
        final double scaleY = MIN_Y_SCALE * plan.getHeight() / locationPicture.getHeight();
        final double scale = Math.min(scaleX, scaleY);
        final int w = (int) Math.round(scale * locationPicture.getWidth());
        final int h = (int) Math.round(scale * locationPicture.getHeight());
        final int x = location.x - w / 2 + (int) Math.round(scale * locationTipShift.x);
        final int y = location.y - h + (int) Math.round(scale * locationTipShift.y);

        final BufferedImage editedPlan = ImageUtils.copy(plan);
        final Graphics2D g = editedPlan.createGraphics();
        g.drawImage(locationPicture, x, y, w, h, null);

        LOG.info("Drawing location on BufferedImage finished in " + (System.currentTimeMillis() - start));

        return plan;
    }
}
