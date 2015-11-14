package ru.hypernavi.core.session;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.servlet.ServletRequest;
import java.util.Optional;
import java.util.function.Function;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by amosov-f on 24.10.15.
 */
public abstract class RequestParam<T> {
    private static final Log LOG = LogFactory.getLog(RequestParam.class);

    public static final RequestParam<String> PRAM_TEXT = new StringParam("text");

    public static final RequestParam<Double> PARAM_LON = new DoubleParam("lon");
    public static final RequestParam<Double> PARAM_LAT = new DoubleParam("lat");

    @NotNull
    private final String name;

    protected RequestParam(@NotNull final String name) {
        this.name = name;
    }

    @Nullable
    public final T getValue(@NotNull final ServletRequest req) {
        final String value = req.getParameter(name);
        try {
            return Optional.ofNullable(value).map(this::parse).orElse(null);
        } catch (RuntimeException e) {
            LOG.warn("Invalid request param '" + name + "=" + value + "'", e);
            return null;
        }
    }

    @Nullable
    abstract T parse(@NotNull final String value);

    public static class LambdaParam<T> extends RequestParam<T> {
        @NotNull
        private final Function<String, T> parser;

        public LambdaParam(@NotNull final String name, @NotNull final Function<String, T> parser) {
            super(name);
            this.parser = parser;
        }

        @Nullable
        @Override
        T parse(@NotNull final String value) {
            return parser.apply(value);
        }
    }

    public static final class StringParam extends LambdaParam<String> {
        public StringParam(@NotNull final String name) {
            super(name, Function.<String>identity());
        }
    }

    public static final class IntegerParam extends LambdaParam<Integer> {
        public IntegerParam(@NotNull final String name) {
            super(name, Integer::parseInt);
        }
    }

    public static final class DoubleParam extends LambdaParam<Double> {
        public DoubleParam(@NotNull final String name) {
            super(name, Double::parseDouble);
        }
    }
}
