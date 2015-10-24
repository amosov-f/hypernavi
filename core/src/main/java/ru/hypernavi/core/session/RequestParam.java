package ru.hypernavi.core.session;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.servlet.ServletRequest;
import java.util.Optional;
import java.util.function.Function;

/**
 * Created by amosov-f on 24.10.15.
 */
public abstract class RequestParam<T> {
    public static final RequestParam<String> PRAM_TEXT = new RequestParam.StringParam("text");

    @NotNull
    private final String name;

    protected RequestParam(@NotNull final String name) {
        this.name = name;
    }

    @Nullable
    public T getValue(@NotNull final ServletRequest req) {
        return Optional.ofNullable(req.getParameter(name)).map(this::parse).orElse(null);
    }

    @Nullable
    public abstract T parse(@NotNull final String value);

    public static class Lambda<T> extends RequestParam<T> {
        @NotNull
        private final Function<String, T> parser;

        public Lambda(@NotNull final String name, @NotNull final Function<String, T> parser) {
            super(name);
            this.parser = parser;
        }

        @Nullable
        @Override
        public T parse(@NotNull final String value) {
            return parser.apply(value);
        }
    }

    public static final class StringParam extends Lambda<String> {
        public StringParam(@NotNull final String name) {
            super(name, Function.<String>identity());
        }
    }
}
