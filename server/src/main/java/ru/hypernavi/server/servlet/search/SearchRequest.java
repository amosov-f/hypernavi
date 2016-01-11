package ru.hypernavi.server.servlet.search;

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
public class SearchRequest extends RequestReader {
    public static final Property<Integer> PAGE = new Property<>("page");
    public static final Property<Integer> NUM_SITE = new Property<>("num_site");

    private static final RequestParam<Integer> PAGE_PARAM = new RequestParam.IntegerParam("p").defaultValue(0);
    private static final RequestParam<Integer> NUM_SITE_PARAM = new RequestParam.IntegerParam("ns").defaultValue(10);

    @Inject
    public SearchRequest(@Assisted @NotNull final HttpServletRequest req) {
        super(req);
    }

    @Override
    public void initialize(@NotNull final Session session) {
        super.initialize(session);
        setPropertyIfPresent(session, PAGE, PAGE_PARAM);
        setPropertyIfPresent(session, NUM_SITE, NUM_SITE_PARAM);
    }

    @Override
    public void validate(@NotNull final Session session) throws SessionValidationException {
        if (!session.has(Property.GEO_LOCATION) && !session.has(Property.TEXT)) {
            throw new SessionValidationException("No location in request!");
        }
    }

    @NotNull
    public static Module module() {
        return new FactoryModuleBuilder().build(Key.get(Types.newParameterizedTypeWithOwner(RequestReader.class, Factory.class, SearchRequest.class)));
    }
}
