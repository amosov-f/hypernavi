package ru.hypernavi.core.session;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;


import org.apache.poi.hssf.record.formula.functions.T;
import ru.hypernavi.core.session.param.Param;

/**
 * User: amosov-f
 * Date: 13.05.16
 * Time: 10:40
 */
public final class ParamRequestReader implements SessionInitializer {
    @NotNull
    private final RequestReader delegate;
    @NotNull
    private final Map<Property<?>, Param<?>> map = new HashMap<>();

    public <P> ParamRequestReader(@NotNull final RequestReader delegate,
                                  @NotNull final Property<P> property,
                                  @NotNull final Param<? extends P> param)
    {
        this.delegate = delegate;
        map.put(property, param);
    }

    @Override
    public void initialize(@NotNull final Session session) {
        delegate.initialize(session);
        map.forEach((property, param) -> delegate.setPropertyIfPresent(session, (Property<T>) property, (Param<T>) param));
    }

    @Override
    public void validate(@NotNull final Session session) throws SessionValidationException {
        delegate.validate(session);
        final Property<?>[] properties = map.keySet().toArray(new Property<?>[map.size()]);
        validate(session, properties);
    }
}
