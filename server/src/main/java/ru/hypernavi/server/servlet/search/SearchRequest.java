package ru.hypernavi.server.servlet.search;

import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;


import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.util.Types;
import ru.hypernavi.core.session.Property;
import ru.hypernavi.core.session.RequestReader;
import ru.hypernavi.core.session.Session;
import ru.hypernavi.core.session.SessionValidationException;
import ru.hypernavi.core.session.param.Param;
import ru.hypernavi.core.session.param.QueryParam;

/**
 * Created by amosov-f on 08.11.15.
 */
public class SearchRequest extends RequestReader {
    public static final Property<Integer> PAGE = new Property<>("page");
    public static final Property<Integer> NUM_SITE = new Property<>("num_site");

    private static final Param<Integer> PAGE_PARAM = new QueryParam.IntegerParam("p").defaultValue(0);
    private static final Param<Integer> NUM_SITE_PARAM = new QueryParam.IntegerParam("ns").defaultValue(10);

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
}
