package amosov;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


import com.mysql.fabric.jdbc.FabricMySQLDriver;
import ru.hypernavi.commons.GeoObject;
import ru.hypernavi.commons.Plan;
import ru.hypernavi.commons.Site;
import ru.hypernavi.core.database.provider.sql.GeoObjectSqlProvider;
import ru.hypernavi.core.database.provider.sql.GeoPointSqlProvider;
import ru.hypernavi.core.database.provider.sql.PlanSqlProvider;
import ru.hypernavi.core.database.provider.sql.SiteSqlProvider;
import ru.hypernavi.util.GeoPointImpl;

/**
 * Created by amosov-f on 22.11.15.
 */
public class MysqlTest {
    public static void main(String[] args) throws SQLException {
        DriverManager.registerDriver(new FabricMySQLDriver());
            // TODO: policy about reconnect
        final String url = "jdbc:mysql://localhost:3306/hypernavi";
        final Connection conn = DriverManager.getConnection(url, "root", "");
        final GeoPointSqlProvider geoPointProvider = new GeoPointSqlProvider(conn);
        final GeoObjectSqlProvider geoObjectProvider = new GeoObjectSqlProvider(conn, geoPointProvider);
        final PlanSqlProvider planProvider = new PlanSqlProvider(conn);
        final SiteSqlProvider siteProvider = new SiteSqlProvider(conn, planProvider, geoObjectProvider);
        final int id = siteProvider.insert(new Site(new GeoObject("name", "descr", new GeoPointImpl(30, 60)), new Plan("http://link1", 1.0), new Plan("http://link2", null)));

        System.out.println(id);
        System.out.println(siteProvider.add(new Site(new GeoObject("name", "descr", new GeoPointImpl(31, 61)), new Plan("http://link1", 1.0), new Plan("http://link2", null))));

//        siteProvider.delete(1);
    }
}
