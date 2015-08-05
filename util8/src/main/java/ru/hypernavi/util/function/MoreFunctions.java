package ru.hypernavi.util.function;

import org.jetbrains.annotations.NotNull;

import java.util.function.BinaryOperator;

/**
 * User: amosov-f
 * Date: 06.08.15
 * Time: 1:23
 */
public enum MoreFunctions {
    ;

    @NotNull
    public static <T> BinaryOperator<T> rightProjection() {
        return (t1, t2) -> t2;
    }
}
