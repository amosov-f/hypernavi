package ru.hypernavi.core.session;

import org.jetbrains.annotations.NotNull;
import ru.hypernavi.core.session.param.Param;

import java.util.HashMap;
import java.util.Map;

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

    public <P1, P2> ParamRequestReader(@NotNull final RequestReader delegate,
                                       @NotNull final Property<P1> property1,
                                       @NotNull final Param<? extends P1> param1,
                                       @NotNull final Property<P2> property2,
                                       @NotNull final Param<? extends P2> param2)
    {
        this.delegate = delegate;
        map.put(property1, param1);
        map.put(property2, param2);
    }

    @Override
    public void initialize(@NotNull final Session session) {
        delegate.initialize(session);
        map.forEach((property, param) -> delegate.setPropertyIfPresent(session, property, (Param) param));
    }

    @Override
    public void validate(@NotNull final Session session) throws SessionValidationException {
        delegate.validate(session);
        if (map.size() == 1) {
            final Property<?>[] properties = map.keySet().toArray(new Property<?>[map.size()]);
            validate(session, properties);
        }
    }
}
