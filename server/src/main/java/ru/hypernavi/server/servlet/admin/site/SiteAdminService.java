package ru.hypernavi.server.servlet.admin.site;

import com.google.inject.Inject;
import org.jetbrains.annotations.NotNull;
import ru.hypernavi.commons.PointMap;
import ru.hypernavi.commons.Site;
import ru.hypernavi.commons.hint.Hint;
import ru.hypernavi.commons.hint.Plan;
import ru.hypernavi.core.auth.VkUser;
import ru.hypernavi.core.database.provider.SiteProvider;
import ru.hypernavi.core.session.Property;
import ru.hypernavi.core.session.Session;
import ru.hypernavi.ml.factor.Factor;
import ru.hypernavi.ml.regression.WekaRegression;
import ru.hypernavi.ml.regression.map.MapFactors;
import ru.hypernavi.ml.regression.map.MapProjectionImpl;
import ru.hypernavi.server.servlet.AbstractHttpService;

import java.util.List;

/**
 * Created by amosov-f on 11.12.15.
 */
public abstract class SiteAdminService extends AbstractHttpService {
    @Inject
    protected SiteProvider provider;

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

    protected void learnAndSerializeModels(@NotNull final Site site) {
        for (final Hint hint : site.getHints()) {
            if (hint instanceof Plan) {
                final Plan plan = (Plan) hint;
                final PointMap[] points = plan.getPoints();
                if (points.length != 0) {
                    final List<? extends Factor<PointMap>> features = MapFactors.linearLonLat(points);
                    final WekaRegression<PointMap> fx = MapProjectionImpl.learn(features, MapFactors.X, points).getClassifier();
                    final WekaRegression<PointMap> fy = MapProjectionImpl.learn(features, MapFactors.Y, points).getClassifier();
                    plan.setModel(Plan.X_MODEL_KEY, fx.serialize());
                    plan.setModel(Plan.Y_MODEL_KEY, fy.serialize());
                }
            }
        }
    }
}
