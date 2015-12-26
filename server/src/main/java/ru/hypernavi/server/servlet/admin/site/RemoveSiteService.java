package ru.hypernavi.server.servlet.admin.site;

import org.jetbrains.annotations.NotNull;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


import org.apache.http.HttpStatus;
import ru.hypernavi.commons.Site;
import ru.hypernavi.core.auth.AdminRequestReader;
import ru.hypernavi.core.session.*;

/**
 * Created by amosov-f on 11.12.15.
 */
@WebServlet(name = "remove site", value = "/admin/site/remove")
public class RemoveSiteService extends SiteAdminService {
    private static final RequestParam<String> SITE_ID_PARAM = new RequestParam.StringParam("site_id");
    private static final Property<String> SITE_ID = new Property<>("site_id");

    public RemoveSiteService() {
        super(new RequestReader.Factory<AdminRequestReader>() {
            @NotNull
            @Override
            public AdminRequestReader create(@NotNull final HttpServletRequest req) {
                return new AdminRequestReader(req) {
                    @Override
                    public void initialize(@NotNull final Session session) {
                        super.initialize(session);
                        setPropertyIfPresent(session, SITE_ID, SITE_ID_PARAM);
                    }

                    @Override
                    public void validate(@NotNull final Session session) throws SessionValidationException {
                        super.validate(session);
                        validate(session, SITE_ID);
                    }
                };
            }
        });
    }

    @Override
    public void service(@NotNull final Session session, @NotNull final HttpServletResponse resp) throws IOException {
        final Site site = provider.remove(session.demand(SITE_ID));
        if (site == null) {
            resp.sendError(HttpStatus.SC_NOT_MODIFIED, "Site wasn't removed!");
            return;
        }
        write(site, resp);
    }
}
