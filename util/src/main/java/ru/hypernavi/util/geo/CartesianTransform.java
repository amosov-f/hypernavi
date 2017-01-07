package ru.hypernavi.util.geo;

import org.jetbrains.annotations.NotNull;
import ru.hypernavi.util.GeoPoint;

import java.awt.geom.Point2D;

/**
 * Created by amosov-f on 04.01.17
 */
public interface CartesianTransform {
  @NotNull
  Point2D transform(@NotNull GeoPoint p);
}
