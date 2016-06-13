package ru.hypernavi.server.servlet.site;

import com.google.inject.Inject;
import freemarker.template.TemplateModelException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.hypernavi.commons.Site;
import ru.hypernavi.core.auth.VkAuthRequestReader;
import ru.hypernavi.core.database.provider.SiteProvider;
import ru.hypernavi.core.session.ParamRequestReader;
import ru.hypernavi.core.session.Property;
import ru.hypernavi.core.session.Session;
import ru.hypernavi.core.session.SessionInitializer;
import ru.hypernavi.server.servlet.HtmlPageHttpService;
import ru.hypernavi.server.servlet.admin.site.SiteProperty;
import ru.hypernavi.util.ArrayGeoPoint;
import ru.hypernavi.util.MoreReflectionUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by amosov-f on 13.06.16.
 */
@WebServlet(name = "site page", value = "/site")
public class SitePage extends HtmlPageHttpService {
    static {
        MoreReflectionUtils.load(ArrayGeoPoint.class);
        MoreReflectionUtils.load(Site.class);
    }

    @Inject
    protected SiteProvider provider;

    @NotNull
    @Override
    protected SessionInitializer createReader(@NotNull final HttpServletRequest req) {
        return new ParamRequestReader(new VkAuthRequestReader(req), SiteProperty.SITE_ID, SiteProperty.SITE_ID_PARAM);
    }

    @NotNull
    @Override
    public String getPathInBundle(@NotNull final Session session) {
        return "site.ftl";
    }

    @Nullable
    @Override
    public Object toDataModel(@NotNull final Session session) throws TemplateModelException {
        final String siteId = session.demand(SiteProperty.SITE_ID);
        final Site site = provider.get(siteId);
        if (site == null) {
            return null;
        }
        final Map<String, Object> model = new HashMap<>();
        model.put("id", siteId);
        model.put("site", site);
        model.put("is_admin", session.get(Property.IS_ADMIN, false));
        model.put("debug", session.get(Property.DEBUG, false));
        return model;
    }
}
