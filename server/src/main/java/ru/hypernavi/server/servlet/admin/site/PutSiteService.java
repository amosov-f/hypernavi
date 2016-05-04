package ru.hypernavi.server.servlet.admin.site;

import org.jetbrains.annotations.NotNull;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import ru.hypernavi.core.session.Session;

/**
 * Created by amosov-f on 06.12.15.
 */
@WebServlet(name = "put site", value = "/admin/site/put")
public final class PutSiteService extends SiteAdminService {
    @Override
    public void service(@NotNull final Session session, @NotNull final HttpServletResponse resp) throws IOException {
        final String id = provider.put(session.demand(SiteRequest.SITE));
        resp.setStatus(HttpStatus.SC_OK);
        resp.setContentType(ContentType.TEXT_PLAIN.getMimeType());
        resp.getWriter().write(id);
    }
}
