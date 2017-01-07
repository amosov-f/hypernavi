package ru.hypernavi.util.geo;

import org.jetbrains.annotations.NotNull;
import ru.hypernavi.util.GeoPoint;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Created by amosov-f on 04.01.17
 */
public final class WGS84Transform implements CartesianTransform {
  @NotNull 
  private final GeoPoint center;

  public WGS84Transform(@NotNull final GeoPoint center) {
    this.center = center;
  }

  @NotNull
  @Override
  public Point2D transform(@NotNull final GeoPoint p) {
    final double phi = Math.toRadians(center.getLatitude());
    final double x = (p.getLongitude() - center.getLongitude()) * lenOfLonDeg(phi);
    final double y = (p.getLatitude() - center.getLatitude()) * lenOfLatDeg(phi);
    return new Point.Double(x, y);
  }

  private static double lenOfLatDeg(final double phi) {
    return 111132.92 - 559.82 * Math.cos(2 * phi) + 1.175 * Math.cos(4 * phi) - 0.0023 * Math.cos(6 * phi);
  }
  
  private static double lenOfLonDeg(final double phi) {
    return 111412.84 * Math.cos(phi) - 93.5 * Math.cos(3 * phi) + 0.118 * Math.cos(5 * phi);
  }
}
