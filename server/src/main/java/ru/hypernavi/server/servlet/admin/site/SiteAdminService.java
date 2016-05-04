package ru.hypernavi.server.servlet.admin.site;

import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;


import com.google.inject.Inject;
import ru.hypernavi.commons.Site;
import ru.hypernavi.core.database.provider.DatabaseProvider;
import ru.hypernavi.core.session.SessionInitializer;
import ru.hypernavi.server.servlet.AbstractHttpService;

/**
 * Created by amosov-f on 11.12.15.
 */
public abstract class SiteAdminService extends AbstractHttpService {
    @Inject
    protected DatabaseProvider<Site> provider;

    @NotNull
    @Override
    protected SessionInitializer createReader(@NotNull final HttpServletRequest req) {
        return new SiteReader(req);
    }
}
