package ru.hypernavi.server.servlet.admin.site;

import org.jetbrains.annotations.NotNull;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


import com.google.inject.Inject;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import ru.hypernavi.core.session.RequestReader;
import ru.hypernavi.core.session.Session;

/**
 * Created by amosov-f on 06.12.15.
 */
@WebServlet(name = "add site", value = "/admin/site/add")
public final class AddSiteService extends SiteAdminService {
    @Inject
    public AddSiteService(@NotNull final RequestReader.Factory<SiteRequest> initFactory) {
        super(initFactory);
    }

    @Override
    public void service(@NotNull final Session session, @NotNull final HttpServletResponse resp) throws IOException {
        final String id = provider.add(session.demand(SiteRequest.SITE));
        if (id == null) {
            resp.sendError(HttpStatus.SC_NOT_MODIFIED, "Site isn't added!");
            return;
        }
        resp.setStatus(HttpStatus.SC_OK);
        resp.setContentType(ContentType.TEXT_PLAIN.getMimeType());
        resp.getWriter().write(id);
    }
}
