package ru.hypernavi.server.servlet.admin.site;

import org.jetbrains.annotations.NotNull;


import com.google.inject.Inject;
import ru.hypernavi.commons.Site;
import ru.hypernavi.core.database.provider.DatabaseProvider;
import ru.hypernavi.core.session.RequestReader;
import ru.hypernavi.server.servlet.AbstractHttpService;

/**
 * Created by amosov-f on 11.12.15.
 */
public abstract class SiteAdminService extends AbstractHttpService {
    @Inject
    protected DatabaseProvider<Site> provider;

    protected SiteAdminService(@NotNull final RequestReader.Factory<SiteRequest> initFactory) {
        super(initFactory);
    }
}
