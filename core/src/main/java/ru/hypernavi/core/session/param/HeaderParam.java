package ru.hypernavi.core.session.param;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;
import java.util.function.Function;


import ru.hypernavi.util.json.GsonUtils;

/**
 * User: amosov-f
 * Date: 03.05.16
 * Time: 14:39
 */
public abstract class HeaderParam<T> extends RequestGetterParam<T> {
    protected HeaderParam(@NotNull final String name) {
        super(name, HttpServletRequest::getHeader);
    }

    public static class LambdaParam<T> extends HeaderParam<T> {
        @NotNull
        private final Function<String, T> parser;

        public LambdaParam(@NotNull final String name, @NotNull final Function<String, T> parser) {
            super(name);
            this.parser = parser;
        }

        @Nullable
        @Override
        public T parse(@NotNull final String value) {
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
