package ru.hypernavi.server.servlet.admin.site;

import org.jetbrains.annotations.NotNull;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


import com.google.gson.reflect.TypeToken;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import ru.hypernavi.commons.Index;
import ru.hypernavi.commons.Site;
import ru.hypernavi.core.auth.AdminRequestReader;
import ru.hypernavi.core.session.*;

/**
 * Created by amosov-f on 20.12.15.
 */
@WebServlet(name = "edit site", value = "/admin/site/edit")
public final class EditSiteService extends SiteAdminService {
    @NotNull
    private static final RequestParam<Index<Site>> SITE_PARAM = new RequestParam.ObjectParam<>("site", new TypeToken<Index<Site>>(){}.getType());
    @NotNull
    private static final Property<Index<Site>> SITE = new Property<>("site");

    public EditSiteService() {
        super(new RequestReader.Factory<AdminRequestReader>() {
            @NotNull
            @Override
            public AdminRequestReader create(@NotNull final HttpServletRequest req) {
                return new AdminRequestReader(req) {
                    @Override
                    public void initialize(@NotNull final Session session) {
                        super.initialize(session);
                        setPropertyIfPresent(session, SITE, SITE_PARAM);
                    }

                    @Override
                    public void validate(@NotNull final Session session) throws SessionValidationException {
                        super.validate(session);
                        validate(session, SITE);
                    }
                };
            }
        });
    }

    @Override
    public void service(@NotNull final Session session, @NotNull final HttpServletResponse resp) throws IOException {
        final Index<Site> site = session.demand(SITE);

        provider.put(site.getId(), site.get());

        resp.setStatus(HttpStatus.SC_OK);
        resp.setContentType(ContentType.TEXT_PLAIN.getMimeType());
        resp.getWriter().write("OK");
    }
}
