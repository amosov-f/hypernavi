package ru.hypernavi.core.session;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.servlet.ServletRequest;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ru.hypernavi.util.json.GsonUtils;

/**
 * Created by amosov-f on 24.10.15.
 */
public abstract class RequestParam<T> {
    private static final Log LOG = LogFactory.getLog(RequestParam.class);

    public static final RequestParam<String> PRAM_TEXT = new StringParam("text");

    public static final RequestParam<Double> PARAM_LON = new DoubleParam("lon");
    public static final RequestParam<Double> PARAM_LAT = new DoubleParam("lat");

    public static final RequestParam<Boolean> DEBUG_PARAM = new RequestParam.BooleanParam("debug").defaultValue(false);

    @NotNull
    private final String name;
    @Nullable
    private T defaultValue;

    protected RequestParam(@NotNull final String name) {
        this.name = name;
        this.defaultValue = null;
    }

    @NotNull
    public RequestParam<T> defaultValue(@NotNull final T defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    @Nullable
    public final T getValue(@NotNull final ServletRequest req) {
        final String value = req.getParameter(name);
        try {
            return Optional.ofNullable(value).map(this::parse).orElse(defaultValue);
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

    public static final class BooleanParam extends LambdaParam<Boolean> {
        public BooleanParam(@NotNull final String name) {
            super(name, value -> Arrays.asList("1", "true").contains(value));
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

    public static final class EnumParam<T extends Enum<T>> extends LambdaParam<T> {
        public EnumParam(@NotNull final Class<T> enumType) {
            this(enumType.getSimpleName().toLowerCase(), enumType);
        }

        public EnumParam(@NotNull final String name, @NotNull final Class<T> enumType) {
            super(name, value -> Enum.valueOf(enumType, value.toUpperCase()));
        }
    }

    public static final class ObjectParam<T> extends LambdaParam<T> {
        public ObjectParam(@NotNull final String name, @NotNull final Type clazz) {
            super(name, value -> GsonUtils.gson().fromJson(value, clazz));
        }
    }
}
