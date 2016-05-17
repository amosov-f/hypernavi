package ru.hypernavi.util;

import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by amosov-f on 17.05.16.
 */
public enum MoreCollections {
    ;

    @NotNull
    public static <K, V> Map<K, V> sortByValue(@NotNull final Map<K, V> map,
                                               @NotNull final Comparator<? super V> comp)
    {
        final Map<K, V> result = new LinkedHashMap<>();
        map.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getValue, comp))
                .forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
        return result;
    }
}
