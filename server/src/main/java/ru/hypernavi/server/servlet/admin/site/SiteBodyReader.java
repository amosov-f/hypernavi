package ru.hypernavi.server.servlet.admin.site;

import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;


import ru.hypernavi.commons.Site;
import ru.hypernavi.core.session.Property;
import ru.hypernavi.core.session.Session;
import ru.hypernavi.core.session.param.BodyParam;
import ru.hypernavi.core.session.param.Param;
import ru.hypernavi.util.json.GsonUtils;

/**
 * User: amosov-f
 * Date: 04.05.16
 * Time: 23:47
 */
public class SiteBodyReader extends SiteReader {
    public static final Property<Site> SITE = new Property<>("site");

    private static final Param<Site> SITE_BODY = new BodyParam.ObjectParam<>(Site.class, GsonUtils::gson, true);

    public SiteBodyReader(@NotNull final HttpServletRequest req) {
        super(req);
    }

    @Override
    public void initialize(@NotNull final Session session) {
        super.initialize(session);
        setPropertyIfPresent(session, SITE, SITE_BODY);
    }
}
