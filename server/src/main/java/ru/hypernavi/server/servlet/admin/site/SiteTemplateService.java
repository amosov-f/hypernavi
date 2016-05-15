package ru.hypernavi.server.servlet.admin.site;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


import com.google.inject.Inject;
import freemarker.template.TemplateModelException;
import ru.hypernavi.commons.Site;
import ru.hypernavi.core.database.provider.SiteProvider;
import ru.hypernavi.core.session.Property;
import ru.hypernavi.core.session.Session;
import ru.hypernavi.core.session.SessionInitializer;
import ru.hypernavi.server.servlet.HtmlPageHttpService;
import ru.hypernavi.util.ArrayGeoPoint;
import ru.hypernavi.util.MoreReflectionUtils;

import static ru.hypernavi.server.servlet.admin.site.SiteBodyReader.SITE;

/**
 * Created by amosov-f on 01.01.16.
 */
@WebServlet(name = "site template", value = "/admin/site")
public class SiteTemplateService extends HtmlPageHttpService {
    static {
        MoreReflectionUtils.load(ArrayGeoPoint.class);
        MoreReflectionUtils.load(Site.class);
    }

    @Inject
    protected SiteProvider provider;

    @NotNull
    @Override
    protected SessionInitializer createReader(@NotNull final HttpServletRequest req) {
        return new SiteBodyReader(req);
    }

    @NotNull
    @Override
    public String getPathInBundle(@NotNull final Session session) {
        return session.has(SITE) || session.get(SiteReader.EDIT, false) ? "edit.ftl" : "site.ftl";
    }

    @Nullable
    @Override
    public Object toDataModel(@NotNull final Session session) throws TemplateModelException {
        final String siteId = session.get(SiteReader.SITE_ID);
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
