package ru.hypernavi.server.servlet.admin.site;

import org.apache.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import ru.hypernavi.commons.Site;
import ru.hypernavi.core.auth.AdminRequestReader;
import ru.hypernavi.core.session.ParamRequestReader;
import ru.hypernavi.core.session.Session;
import ru.hypernavi.core.session.SessionInitializer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by amosov-f on 11.12.15.
 */
//@WebServlet(name = "remove site", value = "/admin/site/remove")
public final class RemoveSiteService extends SiteAdminService {
    @NotNull
    @Override
    protected SessionInitializer createReader(@NotNull final HttpServletRequest req) {
        return new ParamRequestReader(new AdminRequestReader(req), SiteProperty.SITE_ID, SiteProperty.SITE_ID_PARAM);
    }

    @Override
    public void service(@NotNull final Session session, @NotNull final HttpServletResponse resp) throws IOException {
        final Site site = provider.remove(session.demand(SiteProperty.SITE_ID));
        if (site == null) {
            resp.sendError(HttpStatus.SC_NOT_MODIFIED, "Site wasn't removed!");
            return;
        }
        write(site, resp);
    }
}
