package ru.hypernavi.server.servlet.admin.site;

import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.jetbrains.annotations.NotNull;
import ru.hypernavi.commons.Site;
import ru.hypernavi.core.session.Session;
import ru.hypernavi.core.session.SessionInitializer;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by amosov-f on 06.12.15.
 */
@WebServlet(name = "put site", value = "/admin/site/put")
public final class PutSiteService extends SiteAdminService {
    @NotNull
    @Override
    protected SessionInitializer createReader(@NotNull final HttpServletRequest req) {
        return new SiteBodyReader(req);
    }

    @Override
    public void service(@NotNull final Session session, @NotNull final HttpServletResponse resp) throws IOException {
        final Site site = session.demand(SiteBodyReader.SITE);
        setAuthorIfNotPresent(site, session);
        final String id = provider.put(site);
        resp.setStatus(HttpStatus.SC_OK);
        resp.setContentType(ContentType.TEXT_PLAIN.getMimeType());
        resp.getWriter().write(id);
    }
}
