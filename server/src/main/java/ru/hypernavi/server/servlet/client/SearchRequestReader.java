package ru.hypernavi.server.servlet.client;

import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;


import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.util.Types;
import ru.hypernavi.core.session.*;

/**
 * Created by amosov-f on 08.11.15.
 */
public class SearchRequestReader extends RequestReader {
    @Inject
    public SearchRequestReader(@Assisted @NotNull final HttpServletRequest req) {
        super(req);
    }

    @Override
    public void validate(@NotNull final Session session) throws SessionValidationException {
        if (!session.has(Property.GEO_LOCATION)) {
            throw new SessionValidationException("No geo location in request!");
        }
    }

    @NotNull
    public static Module module() {
        return new FactoryModuleBuilder().build(Key.get(Types.newParameterizedTypeWithOwner(RequestReader.class, Factory.class, SearchRequestReader.class)));
    }
}
