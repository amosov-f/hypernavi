package ru.hypernavi.core.session.param;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;


import ru.hypernavi.util.json.GsonUtils;

/**
 * User: amosov-f
 * Date: 03.05.16
 * Time: 14:40
 */
public abstract class CookieParam<T> extends RequestGetterParam<T> {
    private static final Cookie[] EMPTY_ARRAY = new Cookie[0];

    protected CookieParam(@NotNull final String name) {
        super(name, CookieParam::get);
    }

    @Nullable
    private static String get(@NotNull final HttpServletRequest req, @NotNull final String name) {
        return Arrays.stream(Optional.ofNullable(req.getCookies()).orElse(EMPTY_ARRAY))
                .filter(c -> c.getName().equals(name))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    public static class LambdaParam<T> extends CookieParam<T> {
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
            super(name, Function.identity());
        }
    }

    public static final class ObjectParam<T> extends LambdaParam<T> {
        public ObjectParam(@NotNull final String name, @NotNull final Type clazz) {
            super(name, value -> GsonUtils.gson().fromJson(value, clazz));
        }
    }
}
