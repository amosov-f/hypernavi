package ru.hypernavi.server.servlet.admin.site;

import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;


import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.util.Types;
import ru.hypernavi.commons.Index;
import ru.hypernavi.commons.Site;
import ru.hypernavi.core.auth.AdminRequestReader;
import ru.hypernavi.core.session.Property;
import ru.hypernavi.core.session.RequestReader;
import ru.hypernavi.core.session.Session;
import ru.hypernavi.core.session.param.Param;
import ru.hypernavi.core.session.param.QueryParam;

/**
 * Created by amosov-f on 28.12.15.
 */
public final class SiteRequest extends AdminRequestReader {
    static final Property<Site> SITE = new Property<>("site");
    static final Property<Index<Site>> SITE_INDEX = new Property<>("site_index");
    static final Property<String> SITE_ID = new Property<>("site_id");
    static final Property<Boolean> EDIT = new Property<>("edit");

    private static final Param<Site> SITE_PARAM = new QueryParam.ObjectParam<>("site", Site.class);
    private static final Param<Index<Site>> SITE_INDEX_PARAM = new QueryParam.ObjectParam<>("site_index", new TypeToken<Index<Site>>(){}.getType());
    private static final Param<String> SITE_ID_PARAM = new QueryParam.StringParam("site_id");
    private static final Param<Boolean> EDIT_PARAM = new QueryParam.BooleanParam("edit");

    @Inject
    public SiteRequest(@Assisted @NotNull final HttpServletRequest req) {
        super(req);
    }

    @Override
    public void initialize(@NotNull final Session session) {
        super.initialize(session);
        setPropertyIfPresent(session, SITE, SITE_PARAM);
        setPropertyIfPresent(session, SITE_INDEX, SITE_INDEX_PARAM);
        setPropertyIfPresent(session, SITE_ID, SITE_ID_PARAM);
        setPropertyIfPresent(session, EDIT, EDIT_PARAM);
    }

    @NotNull
    public static Module module() {
        return new FactoryModuleBuilder().build(Key.get(Types.newParameterizedTypeWithOwner(RequestReader.class, Factory.class, SiteRequest.class)));
    }
}
