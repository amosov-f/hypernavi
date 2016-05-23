package ru.hypernavi.server.servlet.admin;

import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.Ints;
import com.google.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.hypernavi.core.auth.AdminRequestReader;
import ru.hypernavi.core.auth.VkUser;
import ru.hypernavi.core.database.provider.SiteProvider;
import ru.hypernavi.core.session.Session;
import ru.hypernavi.core.session.SessionInitializer;
import ru.hypernavi.core.webutil.yandex.VkApi;
import ru.hypernavi.server.servlet.HtmlPageHttpService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * User: amosov-f
 * Date: 16.05.16
 * Time: 0:50
 */
@WebServlet(name = "molodec service", value = "/admin/molodec")
public final class MolodecService extends HtmlPageHttpService {
    @Inject
    private SiteProvider provider;
    @Inject
    private VkApi vkApi;
    @Inject
    private ScoreCounter scoreCounter;

    @NotNull
    @Override
    protected SessionInitializer createReader(@NotNull final HttpServletRequest req) {
        return new AdminRequestReader(req);
    }

    @NotNull
    @Override
    public String getPathInBundle(@NotNull final Session session) {
        return "molodec.ftl";
    }

    @Nullable
    @Override
    public Object toDataModel(@NotNull final Session session) {
        final Map<Integer, Score> author2score = scoreCounter.count(provider.getAllSites());
        final VkUser[] authors = vkApi.getUser(Ints.toArray(author2score.keySet()));
        final Score[] scores = author2score.values().toArray(new Score[author2score.size()]);
        return ImmutableMap.of("authors", authors, "scores", scores);
    }
}
