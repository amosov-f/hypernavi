package ru.hypernavi.core.session.param;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * User: amosov-f
 * Date: 03.05.16
 * Time: 23:15
 */
public interface Param<T> {
    Param<Locale> LANG = new QueryParam.LocaleParam("lang").defaultValue(new Locale("ru", "RU"));

    Param<String> TEXT = new QueryParam.StringParam("text");

    Param<Double> LAT = new QueryParam.DoubleParam("lat");
    Param<Double> LON = new QueryParam.DoubleParam("lon");
    Param<Integer> ZOOM = new QueryParam.IntegerParam("zoom");

    Param<String> URL = new QueryParam.StringParam("url");
    Param<String> LINK = new QueryParam.StringParam("link");
    Param<Integer> X = new QueryParam.IntegerParam("x");
    Param<Integer> Y = new QueryParam.IntegerParam("y");

    Param<Boolean> DEBUG = new QueryParam.BooleanParam("debug").defaultValue(false);

    @Nullable
    T getValue(@NotNull HttpServletRequest req);

    default boolean contained(@NotNull final HttpServletRequest req) {
        return getValue(req) != null;
    }
}
