package ru.hypernavi.ml.regression.map;

import org.jetbrains.annotations.NotNull;
import ru.hypernavi.commons.PointMap;
import ru.hypernavi.ml.factor.Factor;
import ru.hypernavi.util.GeoPoint;
import ru.hypernavi.util.GeoPointImpl;
import ru.hypernavi.util.geo.CartesianTransform;
import ru.hypernavi.util.geo.WGS84Transform;

import java.util.Arrays;
import java.util.List;

/**
 * Created by amosov-f on 04.01.17
 */
public enum MapFactors {
  ;

  public static final Factor<PointMap> LONGITUDE = new Factor.Lambda<>("lon", p -> p.getGeoPoint().getLongitude());
  public static final Factor<PointMap> LATITUDE = new Factor.Lambda<>("lat", p -> p.getGeoPoint().getLatitude());

  public static final List<? extends Factor<PointMap>> RAW_LON_LAT = Arrays.asList(LONGITUDE, LATITUDE);

  public static final Factor<PointMap> X = new Factor.Lambda<>("x", p -> p.getMapPoint().getX());
  public static final Factor<PointMap> Y = new Factor.Lambda<>("y", p -> p.getMapPoint().getY());

  @NotNull
  public static List<? extends Factor<PointMap>> linearLonLat(@NotNull final PointMap... points) {
    final double minLon = Arrays.stream(points)
        .map(PointMap::getGeoPoint)
        .mapToDouble(GeoPoint::getLongitude)
        .min()
        .orElseThrow(() -> new IllegalStateException("Can't compute center by zero points!"));
    final double minLat = Arrays.stream(points)
        .map(PointMap::getGeoPoint)
        .mapToDouble(GeoPoint::getLatitude)
        .min()
        .orElseThrow(() -> new IllegalStateException("Can't compute center by zero points!"));
    return linearLonLat(new GeoPointImpl(minLon, minLat));
  }

  @NotNull
  public static List<? extends Factor<PointMap>> linearLonLat(@NotNull final GeoPoint center) {
    final CartesianTransform t = new WGS84Transform(center);
    final Factor<PointMap> fx = new Factor.Lambda<>("linear-lon", p -> t.transform(p.getGeoPoint()).getX());
    final Factor<PointMap> fy = new Factor.Lambda<>("linear-lat", p -> t.transform(p.getGeoPoint()).getY());
    return Arrays.asList(fx, fy);
  }
}
