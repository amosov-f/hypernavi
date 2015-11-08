package ru.hypernavi.server.servlet.client;

import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;


import ru.hypernavi.core.session.Property;
import ru.hypernavi.core.session.RequestReader;
import ru.hypernavi.core.session.Session;
import ru.hypernavi.core.session.SessionInitializationException;

/**
 * Created by amosov-f on 08.11.15.
 */
public class SearchRequestReader extends RequestReader {
    public SearchRequestReader(@NotNull final HttpServletRequest req) {
        super(req);
    }

    @Override
    public void validate(@NotNull final Session session) throws SessionInitializationException {
        if (!session.has(Property.GEO_LOCATION)) {
            throw new SessionInitializationException("No geo location in request!");
        }
    }
}
