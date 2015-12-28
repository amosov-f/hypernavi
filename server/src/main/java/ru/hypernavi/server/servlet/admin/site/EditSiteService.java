package ru.hypernavi.server.servlet.admin.site;

import org.jetbrains.annotations.NotNull;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


import com.google.inject.Inject;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import ru.hypernavi.commons.Index;
import ru.hypernavi.commons.Site;
import ru.hypernavi.core.session.RequestReader;
import ru.hypernavi.core.session.Session;

/**
 * Created by amosov-f on 20.12.15.
 */
@WebServlet(name = "edit site", value = "/admin/site/edit")
public final class EditSiteService extends SiteAdminService {
    @Inject
    public EditSiteService(@NotNull final RequestReader.Factory<SiteRequest> initFactory) {
        super(initFactory);
    }

    @Override
    public void service(@NotNull final Session session, @NotNull final HttpServletResponse resp) throws IOException {
        final Index<Site> site = session.demand(SiteRequest.SITE_INDEX);

        provider.put(site.getId(), site.get());

        resp.setStatus(HttpStatus.SC_OK);
        resp.setContentType(ContentType.TEXT_PLAIN.getMimeType());
        resp.getWriter().write("OK");
    }
}
