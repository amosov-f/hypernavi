package ru.hypernavi.core.session;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;


import com.google.common.base.Joiner;
import org.apache.commons.lang3.ArrayUtils;
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

    public static final RequestParam<Boolean> PARAM_DEBUG = new RequestParam.BooleanParam("debug").defaultValue(false);

    @NotNull
    private final String name;

    protected RequestParam(@NotNull final String name) {
        this.name = name;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @Nullable
    public abstract T getValue(@NotNull final HttpServletRequest req);

    public boolean contained(@NotNull final HttpServletRequest req) {
        return getValue(req) != null;
    }

    public abstract static class RequestParamBase<T> extends RequestParam<T> {
        @NotNull
        private final BiFunction<HttpServletRequest, String, String> getter;
        @Nullable
        private T defaultValue;

        protected RequestParamBase(@NotNull final String name, @NotNull final BiFunction<HttpServletRequest, String, String> getter) {
            super(name);
            this.getter = getter;
        }

        @Nullable
        public T getDefaultValue() {
            return defaultValue;
        }

        @NotNull
        public RequestParam<T> defaultValue(@NotNull final T defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        @Nullable
        @Override
        public final T getValue(@NotNull final HttpServletRequest req) {
            final String value = getter.apply(req, getName());
            try {
                return Optional.ofNullable(value).map(this::parse).orElse(getDefaultValue());
            } catch (RuntimeException e) {
                LOG.warn("Invalid request param '" + getName() + "=" + value + "'", e);
                return null;
            }
        }

        @Nullable
        abstract T parse(@NotNull final String value);
    }

    public abstract static class RequestParamImpl<T> extends RequestParamBase<T> {
        protected RequestParamImpl(@NotNull final String name) {
            super(name, ServletRequest::getParameter);
        }
    }

    public abstract static class ListParam<T> extends RequestParam<List<T>> {
        protected ListParam(@NotNull final String name) {
            super(name);
        }

        @NotNull
        @Override
        public List<T> getValue(@NotNull final HttpServletRequest req) {
            final String[] values = Optional.ofNullable(req.getParameterValues(getName())).orElse(ArrayUtils.EMPTY_STRING_ARRAY);
            try {
                return Arrays.stream(values).map(this::parse).flatMap(List::stream).collect(Collectors.toList());
            } catch (RuntimeException e) {
                LOG.warn("Invalid request param '" + getName() + "=" + Arrays.toString(values) + "'", e);
                return Collections.emptyList();
            }
        }

        @Nullable
        abstract List<T> parse(@NotNull final String value);
    }

    public abstract static class HeaderParam<T> extends RequestParamBase<T> {
        protected HeaderParam(@NotNull final String name) {
            super(name, HttpServletRequest::getHeader);
        }
    }

    public static class LambdaParam<T> extends RequestParamImpl<T> {
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

    public abstract static class DelimiterParam<T> extends ListParam<T> {
        private static final Character[] DELIMITERS = {','};

        protected DelimiterParam(@NotNull final String name) {
            super(name);
        }

        @Nullable
        @Override
        List<T> parse(@NotNull final String value) {
            return Arrays.stream(value.split("[" + Joiner.on("").join(DELIMITERS) + "]"))
                    .map(this::parsePart)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

        @Nullable
        public abstract T parsePart(@NotNull final String part);
    }

    public static class LambdaDelimiterParam<T> extends DelimiterParam<T> {
        @NotNull
        private final Function<String, T> parser;

        protected LambdaDelimiterParam(@NotNull final String name, @NotNull final Function<String, T> parser) {
            super(name);
            this.parser = parser;
        }

        @Nullable
        @Override
        public T parsePart(@NotNull final String part) {
            return parser.apply(part);
        }
    }

    public static final class StringListParam extends LambdaDelimiterParam<String> {
        public StringListParam(@NotNull final String name) {
            super(name, Function.identity());
        }
    }

    public static class LambdaHeaderParam<T> extends HeaderParam<T> {
        @NotNull
        private final Function<String, T> parser;

        public LambdaHeaderParam(@NotNull final String name, @NotNull final Function<String, T> parser) {
            super(name);
            this.parser = parser;
        }

        @Nullable
        @Override
        public T parse(@NotNull final String value) {
            return parser.apply(value);
        }
    }

    public static final class StringHeaderParam extends LambdaHeaderParam<String> {
        public StringHeaderParam(@NotNull final String name) {
            super(name, Function.identity());
        }
    }

    public static final class ObjectHeaderParam<T> extends LambdaHeaderParam<T> {
        public ObjectHeaderParam(@NotNull final String name, @NotNull final Type clazz) {
            super(name, value -> GsonUtils.gson().fromJson(value, clazz));
        }
    }
}
