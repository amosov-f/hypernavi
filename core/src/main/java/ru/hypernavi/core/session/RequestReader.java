package ru.hypernavi.core.session;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;


import ru.hypernavi.util.GeoPoint;

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
        session.setIfNotNull(Property.HTTP_PATH_INFO, req.getPathInfo());

        setPropertyIfPresent(session, Property.TEXT, RequestParam.PRAM_TEXT);
        Optional.ofNullable(getGeoLocation()).ifPresent(location -> session.set(Property.GEO_LOCATION, location));
    }

    @Override
    public void validate(@NotNull final Session session) throws SessionInitializationException {
    }

    @Nullable
    private GeoPoint getGeoLocation() {
        final Double lon = RequestParam.PARAM_LON.getValue(req);
        final Double lat = RequestParam.PARAM_LAT.getValue(req);
        return lat != null && lon != null ? new GeoPoint(lon, lat) : null;
    }

    protected <T> void setPropertyIfPresent(@NotNull final Session session,
                                            @NotNull final Property<T> property,
                                            @NotNull final RequestParam<? extends T> param)
    {
        session.setIfNotNull(property, param.getValue(req));
    }
}
