package ru.hypernavi.server.servlet.admin.site;

import com.google.inject.Inject;
import org.jetbrains.annotations.NotNull;
import ru.hypernavi.commons.Site;
import ru.hypernavi.commons.hint.Hint;
import ru.hypernavi.core.auth.VkUser;
import ru.hypernavi.core.database.provider.SiteProvider;
import ru.hypernavi.core.session.Property;
import ru.hypernavi.core.session.Session;
import ru.hypernavi.core.session.SessionInitializer;
import ru.hypernavi.server.servlet.AbstractHttpService;

import javax.servlet.http.HttpServletRequest;

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

    protected void setAuthorIfNotPresent(@NotNull final Site site, @NotNull final Session session) {
        final VkUser author = session.get(Property.VK_USER);
        if (author == null) {
            return;
        }
        for (final Hint hint : site.getHints()) {
            if (hint.getAuthorUid() == null) {
                hint.setAuthorUid(author.getUid());
            }
        }
    }
}
