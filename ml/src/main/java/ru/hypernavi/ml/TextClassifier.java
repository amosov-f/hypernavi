package ru.hypernavi.ml;

import org.jetbrains.annotations.NotNull;

/**
 * User: amosov-f
 * Date: 29.06.15
 * Time: 0:48
 */
public interface TextClassifier<T extends Enum<T>> {
    @NotNull
    T classify(@NotNull String text);
}
