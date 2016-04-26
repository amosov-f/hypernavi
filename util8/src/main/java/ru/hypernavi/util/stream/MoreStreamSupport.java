package ru.hypernavi.util.stream;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterators;
import java.util.function.IntFunction;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by amosov-f on 05.12.15.
 */
public enum MoreStreamSupport {
    ;

    @NotNull
    public static <T> Stream<T> stream(@NotNull final Iterable<T> iterable) {
        return stream(iterable.iterator());
    }

    @NotNull
    public static <T> Stream<T> stream(@NotNull final Iterator<T> it) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(it, 0), false);
    }

    @NotNull
    public static <T> T[] toArray(@NotNull final Collection<? extends T> col, @NotNull final IntFunction<T[]> generator) {
        return col.stream().toArray(generator);
    }
}
