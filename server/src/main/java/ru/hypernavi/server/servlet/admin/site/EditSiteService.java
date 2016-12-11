package ru.hypernavi.server.servlet.admin.site;

import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.jetbrains.annotations.NotNull;
import ru.hypernavi.commons.Index;
import ru.hypernavi.commons.Site;
import ru.hypernavi.core.auth.AdminRequestReader;
import ru.hypernavi.core.session.ParamRequestReader;
import ru.hypernavi.core.session.Session;
import ru.hypernavi.core.session.SessionInitializer;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static ru.hypernavi.server.servlet.admin.site.SiteProperty.SITE_INDEX;

/**
 * Created by amosov-f on 20.12.15.
 */
@WebServlet(name = "edit site", value = "/admin/site/edit")
public final class EditSiteService extends SiteAdminService {
    @NotNull
    @Override
    protected SessionInitializer createReader(@NotNull final HttpServletRequest req) {
        return new ParamRequestReader(new AdminRequestReader(req), SITE_INDEX, SiteProperty.SITE_INDEX_BODY);
    }

    @Override
    public void service(@NotNull final Session session, @NotNull final HttpServletResponse resp) throws IOException {
        final Index<Site> site = session.demand(SITE_INDEX);
        setAuthorIfNotPresent(site.get(), session);
        learnAndSerializeModels(site.get());
        provider.put(site.getId(), site.get());

        resp.setStatus(HttpStatus.SC_OK);
        resp.setContentType(ContentType.TEXT_PLAIN.getMimeType());
        resp.getWriter().write("OK");
    }
}
