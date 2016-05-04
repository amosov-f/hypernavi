package ru.hypernavi.server.servlet.admin.site;

import org.jetbrains.annotations.NotNull;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.function.Supplier;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import ru.hypernavi.commons.Index;
import ru.hypernavi.commons.Site;
import ru.hypernavi.core.session.Session;
import ru.hypernavi.core.session.SessionInitializer;
import ru.hypernavi.core.session.SessionValidationException;
import ru.hypernavi.core.session.param.BodyParam;
import ru.hypernavi.core.session.param.Param;
import ru.hypernavi.util.json.GsonUtils;

/**
 * Created by amosov-f on 20.12.15.
 */
@WebServlet(name = "edit site", value = "/admin/site/edit")
public final class EditSiteService extends SiteAdminService {
    private static final Param<Index<Site>> SITE_INDEX_BODY = new BodyParam.ObjectParam<>(new TypeToken<Index<Site>>() {}.getType(), new Supplier<Gson>() {
        @Override
        public Gson get() {
            return GsonUtils.gson();
        }
    }, true);

    @NotNull
    @Override
    protected SessionInitializer createReader(@NotNull final HttpServletRequest req) {
        return new SiteReader(req) {
            @Override
            public void initialize(@NotNull final Session session) {
                super.initialize(session);
                setPropertyIfPresent(session, SITE_INDEX, SITE_INDEX_BODY);
            }

            @Override
            public void validate(@NotNull final Session session) throws SessionValidationException {
                super.validate(session);
                validate(session, SITE_INDEX);
            }
        };
    }

    @Override
    public void service(@NotNull final Session session, @NotNull final HttpServletResponse resp) throws IOException {
        final Index<Site> site = session.demand(SiteReader.SITE_INDEX);

        provider.put(site.getId(), site.get());

        resp.setStatus(HttpStatus.SC_OK);
        resp.setContentType(ContentType.TEXT_PLAIN.getMimeType());
        resp.getWriter().write("OK");
    }
}
