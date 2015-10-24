package ru.hypernavi.core.session;

import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * Created by amosov-f on 24.10.15.
 */
public class RequestReader implements SessionInitializer {
    @NotNull
    private final HttpServletRequest req;

    public RequestReader(@NotNull final HttpServletRequest req) {
        this.req = req;
    }

    @Override
    public void initialize(@NotNull final Session session) {
        setPropertyIfPresent(session, Property.TEXT, RequestParam.PRAM_TEXT);
    }

    public <T> void setPropertyIfPresent(@NotNull final Session session,
                                         @NotNull final Property<T> property,
                                         @NotNull final RequestParam<? extends T> param)
    {
        Optional.ofNullable(param.getValue(req)).ifPresent(value -> session.set(property, value));
    }
}
