package ru.hypernavi.server.servlet.admin.site;

import com.google.inject.Inject;
import freemarker.template.TemplateModelException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.hypernavi.commons.Site;
import ru.hypernavi.core.auth.AdminRequestReader;
import ru.hypernavi.core.database.provider.SiteProvider;
import ru.hypernavi.core.session.ParamRequestReader;
import ru.hypernavi.core.session.Property;
import ru.hypernavi.core.session.Session;
import ru.hypernavi.core.session.SessionInitializer;
import ru.hypernavi.server.servlet.HtmlPageHttpService;
import ru.hypernavi.util.ArrayGeoPoint;
import ru.hypernavi.util.MoreReflectionUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static ru.hypernavi.server.servlet.admin.site.SiteProperty.SITE;
import static ru.hypernavi.server.servlet.admin.site.SiteProperty.SITE_BODY;
import static ru.hypernavi.server.servlet.admin.site.SiteProperty.SITE_ID_PARAM;

/**
 * Created by amosov-f on 01.01.16.
 */
@WebServlet(name = "edit site page", value = "/site/edit")
public final class EditSitePage extends HtmlPageHttpService {
    static {
        MoreReflectionUtils.load(ArrayGeoPoint.class);
        MoreReflectionUtils.load(Site.class);
    }

    @Inject
    private SiteProvider provider;

    @NotNull
    @Override
    protected SessionInitializer createReader(@NotNull final HttpServletRequest req) {
        return new ParamRequestReader(new AdminRequestReader(req), SiteProperty.SITE_ID, SITE_ID_PARAM, SITE, SITE_BODY);
    }

    @NotNull
    @Override
    public String getPathInBundle(@NotNull final Session session) {
        return "edit.ftl";
    }

    @Nullable
    @Override
    public Object toDataModel(@NotNull final Session session) throws TemplateModelException {
        final String siteId = session.get(SiteProperty.SITE_ID);
        final Site site = siteId != null ? provider.get(siteId) : session.get(SITE);
        if (site == null) {
            return null;
        }
        final Map<String, Object> model = new HashMap<>();
        model.put("id", siteId);
        model.put("site", site);
        model.put("debug", session.get(Property.DEBUG, false));
        return model;
    }
}
