package ru.hypernavi.core.webutil;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;


import ru.hypernavi.util.awt.ImageUtils;


/**
 * User: amosov-f
 * Date: 26.04.16
 * Time: 20:33
 */
public enum ImageEditor {
    INSTANCE;

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
        final int w = plan.getWidth();
        final int h = plan.getHeight();
        final int l = Math.max(w, h);
        final BufferedImage editedPlan = ImageUtils.copy(plan);
        final Graphics2D g = editedPlan.createGraphics();
        g.setStroke(new BasicStroke(Math.max(l / 100, 1)));
        g.setColor(Color.RED);
        final int r1 = Math.max(l / 20, 1);
        ImageUtils.drawCircle(g, location, r1);
        final int r2 = Math.max(l  / 80, 1);
        ImageUtils.fillCircle(g, location, r2);
        return plan;
    }
}
