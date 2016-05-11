package ru.hypernavi.ml.regression.map;

import java.awt.*;


import org.junit.Assert;
import org.junit.Test;
import ru.hypernavi.commons.PointMap;
import ru.hypernavi.util.ArrayGeoPoint;

/**
 * User: amosov-f
 * Date: 11.05.16
 * Time: 22:31
 */
public final class MapProjectionImplTest {
    private static final double EPS = 0.000001;

    @Test
    public void validationMustReturnDistancesToPredictedPoints() throws Exception {
        final PointMap p1 = PointMap.of(ArrayGeoPoint.of(60, 30), new Point(0, 0));
        final PointMap p2 = PointMap.of(ArrayGeoPoint.of(61, 31), new Point(10, 10));
        final PointMap p3 = PointMap.of(ArrayGeoPoint.of(62, 32), new Point(20, 30));
        final PointMap p4 = PointMap.of(ArrayGeoPoint.of(64, 32), new Point(40, 30));
        Assert.assertArrayEquals(
                new double[]{32.526911, 14.142135, 17.464249, 34.481879},
                MapProjectionImpl.validate(p1, p2, p3, p4),
                EPS
        );
    }
}