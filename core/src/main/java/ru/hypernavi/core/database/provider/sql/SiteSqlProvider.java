package ru.hypernavi.core.database.provider.sql;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


import com.google.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ru.hypernavi.commons.GeoObject;
import ru.hypernavi.commons.Plan;
import ru.hypernavi.commons.Site;
import ru.hypernavi.util.SqlUtils;

/**
 * Created by amosov-f on 23.11.15.
 */
public final class SiteSqlProvider extends SqlProvider<Site> {
    private static final Log LOG = LogFactory.getLog(SiteSqlProvider.class);

    @NotNull
    private final SqlProvider<GeoObject> geoObjectProvider;
    @NotNull
    private final SqlProvider<Plan> planProvider;

    @Inject
    public SiteSqlProvider(@NotNull final Connection conn,
                           @NotNull final SqlProvider<Plan> planProvider,
                           @NotNull final SqlProvider<GeoObject> geoObjectProvider)
    {
        super(conn);
        this.geoObjectProvider = geoObjectProvider;
        this.planProvider = planProvider;
    }

    @Nullable
    @Override
    public Site select(final int id) throws SQLException {
        final Integer geoObjectId = SqlUtils.integer(statement.executeQuery("SELECT geo_object_id FROM site WHERE id = " + id));
        if (geoObjectId == null) {
            return null;
        }
        final GeoObject position = geoObjectProvider.select(geoObjectId);
        if (position == null) {
            return null;
        }
        final Plan[] plans = SqlUtils.intStream(statement.executeQuery("SELECT plan_id FROM site_plan WHERE site_id = " + id))
                .mapToObj(String::valueOf)
                .map(planProvider::get)
                .toArray(Plan[]::new);
        return new Site(position, plans);
    }

    @Nullable
    @Override
    public Integer insert(@NotNull final Site site) throws SQLException {
        final GeoObject geoObject = site.getPosition();
        final Integer geoObjectId = geoObjectProvider.insert(geoObject);
        if (geoObjectId == null) {
            return null;
        }
        final List<Integer> planIds = new ArrayList<>();
        for (final Plan plan : site.getPlans()) {
            final Integer planId = planProvider.insert(plan);
            if (planId == null) {
                return null;
            }
            planIds.add(planId);
        }
        final PreparedStatement ps = conn.prepareStatement(query("insert", geoObjectId), Statement.RETURN_GENERATED_KEYS);
        ps.executeUpdate();
        final Integer siteId = SqlUtils.integer(ps.getGeneratedKeys());
        if (siteId == null) {
            return null;
        }
        for (final int planId : planIds) {
            statement.execute("INSERT IGNORE INTO site_plan (site_id, plan_id) VALUES (" + siteId + ", " + planId + ")");
        }
        return siteId;
    }

    @Nullable
    @Override
    public Site delete(final int id) throws SQLException {
        final Site site = select(id);
        if (site == null) {
            return null;
        }
        final Integer positionId = SqlUtils.integer(statement.executeQuery("SELECT geo_object_id FROM site WHERE id = " + id));
        if (positionId == null) {
            return null;
        }
        final int[] planIds = SqlUtils.intStream(statement.executeQuery("SELECT plan_id FROM site_plan WHERE site_id = " + id)).toArray();

        statement.execute("DELETE site, site_plan FROM site, site_plan WHERE site.id = " + id + " AND site_plan.site_id = " + id);
        geoObjectProvider.delete(positionId);
        for (final int planId : planIds) {
            planProvider.delete(planId);
        }

        return null;
    }

    @NotNull
    @Override
    protected String query(@NotNull final String name, @NotNull final Object... args) {
        return super.query("/database/site/" + name + ".sql", args);
    }
}
