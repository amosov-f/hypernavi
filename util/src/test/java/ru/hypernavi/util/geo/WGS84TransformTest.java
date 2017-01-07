package ru.hypernavi.util.geo;

import org.junit.Assert;
import org.junit.Test;
import ru.hypernavi.util.GeoPoint;
import ru.hypernavi.util.GeoPointImpl;

import java.awt.geom.Point2D;

/**
 * Created by amosov-f on 05.01.17
 */
public final class WGS84TransformTest {
  @Test
  public void testDistanceCalculation() {
    final GeoPoint p1 = new GeoPointImpl(30.178127, 59.943436);
    final GeoPoint p2 = new GeoPointImpl(30.401888, 59.943522);
    final GeoPoint p3 = new GeoPointImpl(30.179226, 59.985308);
    final CartesianTransform t = new WGS84Transform(p1);
    final Point2D distX = t.transform(p2);
    final Point2D distY = t.transform(p3);
    Assert.assertEquals(12.6, distX.getX() / 1000, 0.1);
    Assert.assertEquals(0, distX.getY() / 1000, 0.1);
    Assert.assertEquals(0, distY.getX() / 1000, 0.1);
    Assert.assertEquals(4.72, distY.getY() / 1000, 0.1);
  }
}