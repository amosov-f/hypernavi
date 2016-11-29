package ru.hypernavi.core.telegram;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.hypernavi.commons.PointMap;
import ru.hypernavi.commons.hint.Plan;
import ru.hypernavi.core.webutil.ImageEditor;
import ru.hypernavi.ml.regression.map.MapProjectionImpl;
import ru.hypernavi.util.GeoPoint;
import ru.hypernavi.util.awt.ImageUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * User: amosov-f
 * Date: 03.05.16
 * Time: 2:44
 */
public enum LocationMapper {
    INSTANCE;

    private static final Log LOG = LogFactory.getLog(LocationMapper.class);

    @Nullable
    public LocationImage mapLocation(@NotNull final Plan plan, @NotNull final GeoPoint geoLocation) {
        final PointMap[] points = plan.getPoints();
        if (points.length == 0) {
            return null;
        }

        final Future<BufferedImage> futurePlanPicture = ImageUtils.downloadAsync(plan.getImage().getLink());

        final long start = System.currentTimeMillis();
        final Point mapLocation = MapProjectionImpl.map(geoLocation, points);
        LOG.info("Location mapping finished in " + (System.currentTimeMillis() - start) + " ms");

        final BufferedImage planPicture;
        try {
            planPicture = futurePlanPicture.get();
        } catch (InterruptedException | ExecutionException e) {
            LOG.warn("Can't download plan picture: " + plan.getImage().getLink(), e);
            return null;
        }
        final BufferedImage map = ImageEditor.INSTANCE.drawLocation(planPicture, mapLocation);
        return new LocationImage(plan, map, mapLocation);
    }
}
