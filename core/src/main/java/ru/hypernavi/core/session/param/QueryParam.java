package ru.hypernavi.core.session.param;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.hypernavi.util.json.GsonUtils;

import javax.servlet.ServletRequest;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Locale;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: amosov-f
 * Date: 03.05.16
 * Time: 14:36
 */
public abstract class QueryParam<T> extends RequestGetterParam<T> {
    protected QueryParam(@NotNull final String name) {
        super(name, ServletRequest::getParameter);
    }

    public static class LambdaParam<T> extends QueryParam<T> {
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
            super(name, Function.identity());
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

    public static final class LocaleParam extends QueryParam<Locale> {
        private static final Pattern SHORT_PATTERN = Pattern.compile("[a-zA-Z]{2}");
        private static final Pattern LONG_PATTERN = Pattern.compile("([a-zA-Z]{2})[-_]([a-zA-Z]{2})");

        public LocaleParam(@NotNull final String name) {
            super(name);
        }

        @Nullable
        @Override
        protected Locale parse(@NotNull final String value) {
            final Matcher longMatcher = LONG_PATTERN.matcher(value);
            if (longMatcher.matches()) {
                final String language = longMatcher.group(1);
                final String country = longMatcher.group(2);
                return new Locale(language, country);
            }
            final Matcher shortMatcher = SHORT_PATTERN.matcher(value);
            if (shortMatcher.matches()) {
                return new Locale(value);
            }
            return null;
        }
    }
}
