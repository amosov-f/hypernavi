package ru.hypernavi.server.servlet.admin.site;

import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;


import com.google.gson.reflect.TypeToken;
import ru.hypernavi.commons.Index;
import ru.hypernavi.commons.Site;
import ru.hypernavi.core.auth.AdminRequestReader;
import ru.hypernavi.core.session.Property;
import ru.hypernavi.core.session.Session;
import ru.hypernavi.core.session.param.BodyParam;
import ru.hypernavi.core.session.param.Param;
import ru.hypernavi.core.session.param.QueryParam;
import ru.hypernavi.util.json.GsonUtils;

/**
 * Created by amosov-f on 28.12.15.
 */
public final class SiteRequest extends AdminRequestReader {
    static final Property<Site> SITE = new Property<>("site");
    static final Property<Index<Site>> SITE_INDEX = new Property<>("site_index");
    static final Property<String> SITE_ID = new Property<>("site_id");
    static final Property<Boolean> EDIT = new Property<>("edit");

    private static final Param<Site> SITE_PARAM = new QueryParam.ObjectParam<>("site", Site.class);
    private static final Param<Site> SITE_BODY = new BodyParam.ObjectParam<>(Site.class, GsonUtils::gson);
    private static final Param<Index<Site>> SITE_INDEX_PARAM = new QueryParam.ObjectParam<>("site_index", new TypeToken<Index<Site>>(){}.getType());
    private static final Param<Index<Site>> SITE_INDEX_BODY = new BodyParam.ObjectParam<>(new TypeToken<Index<Site>>(){}.getType(), GsonUtils::gson);
    private static final Param<String> SITE_ID_PARAM = new QueryParam.StringParam("site_id");
    private static final Param<Boolean> EDIT_PARAM = new QueryParam.BooleanParam("edit");

    public SiteRequest(@NotNull final HttpServletRequest req) {
        super(req);
    }

    @Override
    public void initialize(@NotNull final Session session) {
        super.initialize(session);
        setPropertyIfPresent(session, SITE, SITE_PARAM);
//        setPropertyIfPresent(session, SITE, SITE_BODY);
        setPropertyIfPresent(session, SITE_INDEX, SITE_INDEX_PARAM);
//        setPropertyIfPresent(session, SITE_INDEX, SITE_INDEX_BODY);
        setPropertyIfPresent(session, SITE_ID, SITE_ID_PARAM);
        setPropertyIfPresent(session, EDIT, EDIT_PARAM);
    }
}
