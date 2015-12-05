package ru.hypernavi.core.geoindex;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


import ru.hypernavi.commons.Positioned;
import ru.hypernavi.util.GeoPoint;

/**
 * Created by Константин on 14.08.2015.
 */
public final class DummyGeoIndex<T extends Positioned> implements GeoIndex<T> {
    @NotNull
    private final List<T> points = new ArrayList<>();

    public DummyGeoIndex(@NotNull final T... points) {
        Collections.addAll(this.points, points);
    }

    @NotNull
    @Override
    public List<T> getNN(@NotNull final GeoPoint location, final int offset, final int count) {
        return points.stream()
                .sorted(Comparator.comparing(point -> GeoPoint.distance(location, point.getLocation())))
                .skip(offset)
                .limit(count)
                .collect(Collectors.toList());
    }

    @NotNull
    @Override
    @Deprecated
    public List<T> getAll() {
        return points;
    }

    @Override
    @Deprecated
    public String add(@NotNull final T point) {
        points.add(point);
        return null;
    }
}
