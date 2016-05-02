package ru.hypernavi.core.telegram;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.image.BufferedImage;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ru.hypernavi.commons.PointMap;
import ru.hypernavi.commons.hint.Plan;
import ru.hypernavi.core.webutil.ImageEditor;
import ru.hypernavi.ml.regression.map.MapProjectionImpl;
import ru.hypernavi.util.GeoPoint;
import ru.hypernavi.util.awt.ImageUtils;

/**
 * User: amosov-f
 * Date: 03.05.16
 * Time: 2:44
 */
public enum LocationMapper {
    INSTANCE;

    private static final Log LOG = LogFactory.getLog(LocationMapper.class);

    @Nullable
    public BufferedImage mapLocation(@NotNull final Plan plan, @NotNull final GeoPoint location) {
        final PointMap[] points = plan.getPoints();
        if (points.length == 0) {
            return null;
        }

        final Point point = MapProjectionImpl.map(location, points);

        final BufferedImage image = ImageUtils.downloadSafe(plan.getImage().getLink());
        if (image == null) {
            LOG.warn("Can't download image: " + plan.getImage().getLink());
            return null;
        }
        return ImageEditor.INSTANCE.drawLocation(image, point);
    }
}
