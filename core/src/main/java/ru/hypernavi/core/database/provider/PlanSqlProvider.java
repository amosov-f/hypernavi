package ru.hypernavi.core.database.provider;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;


import ru.hypernavi.commons.Plan;
import ru.hypernavi.util.SqlUtils;

/**
 * Created by amosov-f on 28.11.15.
 */
public final class PlanSqlProvider extends SqlProvider<Plan> {
    public PlanSqlProvider(@NotNull final Connection conn) {
        super(conn);
    }

    @Nullable
    @Override
    public Plan select(final int id) throws SQLException {
        final ResultSet select = statement.executeQuery("SELECT * FROM plan WHERE id = " + id);
        return select.next() ? new Plan(select.getString("link"), select.getDouble("azimuth")) : null;

    }

    @Nullable
    @Override
    public Integer insert(@NotNull final Plan plan) throws SQLException {
        final PreparedStatement ps = conn.prepareStatement(
                query("insert", plan.getLink(), plan.getAzimuth()),
                Statement.RETURN_GENERATED_KEYS
        );
        ps.executeUpdate();
        return SqlUtils.integer(ps.getGeneratedKeys());
    }

    @Nullable
    @Override
    public Plan delete(final int id) throws SQLException {
        final Plan plan = select(id);
        if (plan == null) {
            return null;
        }
        statement.execute("DELETE FROM plan WHERE id = " + id);
        return plan;
    }

    @NotNull
    @Override
    protected String query(@NotNull final String name, @NotNull final Object... args) {
        return super.query("/database/plan/" + name + ".sql", args);
    }
}
