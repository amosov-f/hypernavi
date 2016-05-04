package ru.hypernavi.server.servlet.admin.site;

import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;


import ru.hypernavi.commons.Index;
import ru.hypernavi.commons.Site;
import ru.hypernavi.core.auth.AdminRequestReader;
import ru.hypernavi.core.session.Property;
import ru.hypernavi.core.session.Session;
import ru.hypernavi.core.session.param.Param;
import ru.hypernavi.core.session.param.QueryParam;

/**
 * Created by amosov-f on 28.12.15.
 */
public class SiteReader extends AdminRequestReader {
    static final Property<Index<Site>> SITE_INDEX = new Property<>("site_index");
    static final Property<String> SITE_ID = new Property<>("site_id");
    static final Property<Boolean> EDIT = new Property<>("edit");

    private static final Param<String> SITE_ID_PARAM = new QueryParam.StringParam("site_id");
    private static final Param<Boolean> EDIT_PARAM = new QueryParam.BooleanParam("edit");

    public SiteReader(@NotNull final HttpServletRequest req) {
        super(req);
    }

    @Override
    public void initialize(@NotNull final Session session) {
        super.initialize(session);
        setPropertyIfPresent(session, SITE_ID, SITE_ID_PARAM);
        setPropertyIfPresent(session, EDIT, EDIT_PARAM);
    }
}
