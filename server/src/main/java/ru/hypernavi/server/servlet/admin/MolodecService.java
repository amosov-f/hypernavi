package ru.hypernavi.server.servlet.admin;

import org.jetbrains.annotations.NotNull;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


import com.google.inject.Inject;
import ru.hypernavi.commons.Site;
import ru.hypernavi.commons.hint.Hint;
import ru.hypernavi.commons.hint.Plan;
import ru.hypernavi.core.auth.AdminRequestReader;
import ru.hypernavi.core.database.provider.SiteProvider;
import ru.hypernavi.core.session.Session;
import ru.hypernavi.core.session.SessionInitializer;
import ru.hypernavi.server.servlet.AbstractHttpService;

/**
 * User: amosov-f
 * Date: 16.05.16
 * Time: 0:50
 */
@WebServlet(name = "molodec service", value = "/admin/molodec")
public final class MolodecService extends AbstractHttpService {
    @Inject
    private SiteProvider provider;

    @NotNull
    @Override
    protected SessionInitializer createReader(@NotNull final HttpServletRequest req) {
        return new AdminRequestReader(req);
    }

    @Override
    public void service(@NotNull final Session session, @NotNull final HttpServletResponse resp) throws IOException {
        final Map<Integer, Integer> stats = new HashMap<>();
        for (final Site site : provider.getAllSites()) {
            for (final Hint hint : site.getHints()) {
                final Integer authorUid = hint.getAuthorUid();
                if (authorUid != null && hint instanceof Plan && ((Plan) hint).getPoints().length > 0) {
                    stats.put(authorUid, stats.getOrDefault(authorUid, 0) + 1);
                }
            }
        }
        write(stats, resp);
    }
}
