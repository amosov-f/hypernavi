package ru.hypernavi.core.session.param;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.function.BiFunction;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * User: amosov-f
 * Date: 03.05.16
 * Time: 14:34
 */
public abstract class RequestGetterParam<T> extends NamedParam<T> {
    private static final Log LOG = LogFactory.getLog(RequestGetterParam.class);

    @NotNull
    private final BiFunction<HttpServletRequest, String, String> getter;
    @Nullable
    private T defaultValue;

    protected RequestGetterParam(@NotNull final String name, @NotNull final BiFunction<HttpServletRequest, String, String> getter) {
        super(name);
        this.getter = getter;
    }

    @Nullable
    public T getDefaultValue() {
        return defaultValue;
    }

    @NotNull
    public NamedParam<T> defaultValue(@NotNull final T defaultValue) {
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
