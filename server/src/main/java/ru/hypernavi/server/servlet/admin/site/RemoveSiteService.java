package ru.hypernavi.server.servlet.admin.site;

import org.jetbrains.annotations.NotNull;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


import org.apache.http.HttpStatus;
import ru.hypernavi.commons.Site;
import ru.hypernavi.core.session.Session;

/**
 * Created by amosov-f on 11.12.15.
 */
@WebServlet(name = "remove site", value = "/admin/site/remove")
public final class RemoveSiteService extends SiteAdminService {
    @Override
    public void service(@NotNull final Session session, @NotNull final HttpServletResponse resp) throws IOException {
        final Site site = provider.remove(session.demand(SiteReader.SITE_ID));
        if (site == null) {
            resp.sendError(HttpStatus.SC_NOT_MODIFIED, "Site wasn't removed!");
            return;
        }
        write(site, resp);
    }
}
