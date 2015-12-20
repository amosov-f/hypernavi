package ru.hypernavi.server.servlet.admin;

import org.jetbrains.annotations.NotNull;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


import com.google.inject.Inject;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import ru.hypernavi.commons.Site;
import ru.hypernavi.core.auth.AdminRequestReader;
import ru.hypernavi.core.session.*;

/**
 * Created by amosov-f on 06.12.15.
 */
@WebServlet(name = "add site", value = "/admin/site/add", loadOnStartup = 0)
public class AddSiteAdminService extends SiteAdminService {
    @NotNull
    private static final RequestParam<Site> SITE_PARAM = new RequestParam.ObjectParam<>("site", Site.class);
    @NotNull
    private static final Property<Site> SITE = new Property<>("site");

    @Inject
    protected AddSiteAdminService() {
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
        final String id = provider.add(session.demand(SITE));
        if (id == null) {
            resp.sendError(HttpStatus.SC_NOT_MODIFIED, "Site isn't added!");
            return;
        }
        resp.setStatus(HttpStatus.SC_OK);
        resp.setContentType(ContentType.TEXT_PLAIN.getMimeType());
        resp.getWriter().write(id);
    }
}
