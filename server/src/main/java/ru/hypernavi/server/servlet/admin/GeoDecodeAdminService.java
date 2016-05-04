package ru.hypernavi.server.servlet.admin;

import org.jetbrains.annotations.NotNull;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


import com.google.inject.Inject;
import org.apache.http.HttpStatus;
import ru.hypernavi.commons.GeoObject;
import ru.hypernavi.core.auth.AdminRequestReader;
import ru.hypernavi.core.session.*;
import ru.hypernavi.core.webutil.yandex.GeoDecoder;
import ru.hypernavi.server.servlet.AbstractHttpService;

/**
 * Created by amosov-f on 12.12.15.
 */
@WebServlet(name = "geodecode", value = "/geodecode")
public final class GeoDecodeAdminService extends AbstractHttpService {
    @Inject
    private GeoDecoder decoder;

    @NotNull
    @Override
    protected SessionInitializer createReader(@NotNull final HttpServletRequest req) {
        return new AdminRequestReader(req) {
            @Override
            public void validate(@NotNull final Session session) throws SessionValidationException {
                super.validate(session);
                validate(session, Property.TEXT);
            }
        };
    }

    @Override
    public void service(@NotNull final Session session, @NotNull final HttpServletResponse resp) throws IOException {
        final String text = session.demand(Property.TEXT);

        final GeoObject geoObject = decoder.decode(text);

        if (geoObject == null) {
            resp.sendError(HttpStatus.SC_NOT_FOUND);
        } else {
            write(geoObject, resp);
        }
    }
}
