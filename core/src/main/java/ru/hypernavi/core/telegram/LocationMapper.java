package ru.hypernavi.core.telegram;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.hypernavi.commons.PointMap;
import ru.hypernavi.commons.hint.Plan;
import ru.hypernavi.core.webutil.ImageEditor;
import ru.hypernavi.ml.regression.map.MapFactors;
import ru.hypernavi.ml.regression.map.MapProjection;
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
    public LocationImage drawLocation(@NotNull final Plan plan, @NotNull final GeoPoint geoLocation) {
        final PointMap[] points = plan.getPoints();
        if (points.length == 0) {
            return null;
        }

        final Future<BufferedImage> futurePlanPicture = ImageUtils.downloadAsync(plan.getImage().getLink());

        final Point mapLocation = mapLocation(plan, geoLocation);

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

    @NotNull
    public Point mapLocation(@NotNull final Plan plan, @NotNull final GeoPoint geoLocation) {
        final long start = System.currentTimeMillis();

        final PointMap[] points = plan.getPoints();

        final String xModel = plan.getModel(Plan.X_MODEL_KEY);
        final String yModel = plan.getModel(Plan.Y_MODEL_KEY);
        final MapProjection mapProjection;
//        if (xModel != null && yModel != null) {
//            LOG.debug("Models are in database");
//            mapProjection = MapProjectionImpl.deserialize(xModel, yModel, MapFactors.RAW_LON_LAT);
//        } else {
            LOG.debug("No models in database");
            mapProjection = MapProjectionImpl.learn(MapFactors.linearLonLat(points), points);
//        }

        final Point mapLocation = mapProjection.map(geoLocation);
        LOG.info("Location mapping finished in " + (System.currentTimeMillis() - start) + " ms");

        return mapLocation;
    }

    @Nullable
    public Point mapLocationInside(@NotNull final Plan plan, @NotNull final GeoPoint geoLocation) {
        if (plan.getPoints().length == 0) {
            return null;
        }
        final Point location = mapLocation(plan, geoLocation);
        if (LocationImage.checkLocationInsideImage(plan.getImage(), location)) {
            return location;
        }
        return null;
    }
}
