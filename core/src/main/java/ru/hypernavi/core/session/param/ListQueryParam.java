package ru.hypernavi.core.session.param;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


import com.google.common.base.Joiner;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * User: amosov-f
 * Date: 03.05.16
 * Time: 14:38
 */
public abstract class ListQueryParam<T> extends NamedParam<List<T>> {
    private static final Log LOG = LogFactory.getLog(ListQueryParam.class);

    protected ListQueryParam(@NotNull final String name) {
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

    public abstract static class DelimiterParam<T> extends ListQueryParam<T> {
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
}
