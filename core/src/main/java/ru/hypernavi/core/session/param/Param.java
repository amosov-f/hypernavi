package ru.hypernavi.core.session.param;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.servlet.http.HttpServletRequest;

/**
 * User: amosov-f
 * Date: 03.05.16
 * Time: 23:15
 */
public interface Param<T> {
    Param<String> TEXT = new QueryParam.StringParam("text");

    Param<Double> LAT = new QueryParam.DoubleParam("lat");
    Param<Double> LON = new QueryParam.DoubleParam("lon");

    Param<Boolean> DEBUG = new QueryParam.BooleanParam("debug").defaultValue(false);

    @Nullable
    T getValue(@NotNull HttpServletRequest req);

    default boolean contained(@NotNull final HttpServletRequest req) {
        return getValue(req) != null;
    }
}
