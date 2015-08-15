package ru.hypernavi.core;

import org.jetbrains.annotations.NotNull;


import java.util.*;


import javafx.util.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ru.hypernavi.commons.Hypermarket;
import ru.hypernavi.commons.Positioned;
import ru.hypernavi.util.GeoPoint;

/**
 * Created by Константин on 14.08.2015.
 */
public class ArrayMapStructure<T extends Positioned> implements MapStructure<T> {
    private static final Log LOG = LogFactory.getLog(ArrayMapStructure.class);
    private final T[] listPoints;

    ArrayMapStructure(final T[] points) {
        listPoints = points.clone();
    }

    @NotNull
    @Override
    public T[] find(final GeoPoint position, final int number) {
        final int k = number > size() ? size() : number;
        final T[] result = (T[]) new Hypermarket[k];
        final double[] distance = new double[listPoints.length];
        for (int i = 0; i < listPoints.length; ++i) {
            distance[i] = GeoPoint.distance(position, listPoints[i].getLocation());
        }
        final List<Pair<Double, Integer>> sorted = new ArrayList<>();
        for (int i = 0; i < listPoints.length; ++i) {
            sorted.add(new Pair<>(distance[i], i));
        }
        Collections.sort(sorted, Comparator.comparing(Pair::getKey));

        for (int i = 0; i < k; ++i) {
            result[i] = listPoints[sorted.get(i).getValue()];
        }
        return result;
    }

    @Override
    public int size() {
        return listPoints.length;
    }
}
