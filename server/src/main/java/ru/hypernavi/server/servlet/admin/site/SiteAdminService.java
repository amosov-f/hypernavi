package ru.hypernavi.server.servlet.admin.site;

import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;


import com.google.inject.Inject;
import ru.hypernavi.core.database.provider.SiteProvider;
import ru.hypernavi.core.session.SessionInitializer;
import ru.hypernavi.server.servlet.AbstractHttpService;

/**
 * Created by amosov-f on 11.12.15.
 */
public abstract class SiteAdminService extends AbstractHttpService {
    @Inject
    protected SiteProvider provider;

    @NotNull
    @Override
    protected SessionInitializer createReader(@NotNull final HttpServletRequest req) {
        return new SiteReader(req);
    }
}
