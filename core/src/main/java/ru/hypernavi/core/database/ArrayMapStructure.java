package ru.hypernavi.core.database;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


import javafx.util.Pair;
import ru.hypernavi.commons.Positioned;
import ru.hypernavi.util.GeoPoint;

/**
 * Created by Константин on 14.08.2015.
 */
public class ArrayMapStructure<T extends Positioned> implements MapStructure<T> {
    private final List<T> listPoints = new ArrayList<>();

    public ArrayMapStructure(final T[] points) {
        Collections.addAll(listPoints, points);
    }

    public ArrayMapStructure() {
    }

    @NotNull
    @Override
    public List<T> find(final GeoPoint position, final int number) {
        final int k = number > size() ? size() : number;

        final List<T> result = new ArrayList<>();
        final double[] distance = new double[listPoints.size()];

        for (int i = 0; i < listPoints.size(); ++i)
            distance[i] = GeoPoint.distance(position, listPoints.get(i).getLocation());


        final List<Pair<Double, Integer>> sorted = new ArrayList<>();
        for (int i = 0; i < listPoints.size(); ++i)
            sorted.add(new Pair<>(distance[i], i));

        Collections.sort(sorted, Comparator.comparing(Pair::getKey));

        for (int i = 0; i < k; ++i)
            result.add(listPoints.get(sorted.get(i).getValue()));

        return result;
    }

    @Override
    public int size() {
        return listPoints.size();
    }

    @Override
    public void add(@NotNull final T hyper) {
        listPoints.add(hyper);
    }
}
