package amosov;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import ru.hypernavi.commons.GeoObject;
import ru.hypernavi.commons.Plan;
import ru.hypernavi.commons.Site;
import ru.hypernavi.core.database.provider.mongo.SiteMongoProvider;
import ru.hypernavi.util.GeoPoint;
import ru.hypernavi.util.json.GsonUtil;

/**
 * Created by amosov-f on 05.12.15.
 */
public class MongoTest {
    public static void main(String[] args) {

        MongoClient mongo = new MongoClient("localhost", 27017);

        MongoDatabase db = mongo.getDatabase("hypernavi");

        final SiteMongoProvider provider = new SiteMongoProvider(db);
        final Site site1 = new Site(new GeoObject("name1", "descr1", new GeoPoint(31, 61)), new Plan("http://link1.1", 1.0), new Plan("http://link1.2", null));
        final Site site2 = new Site(new GeoObject("name2", "descr2", new GeoPoint(30, 60)), new Plan("http://link2.1", 1.0), new Plan("http://link2.2", null));
        final Site site3 = new Site(new GeoObject("name3", "descr3", new GeoPoint(32, 62)), new Plan("http://link3.1", 1.0), new Plan("http://link3.2", null));
        provider.add(site1);
        provider.add(site2);
        provider.add(site3);

        System.out.println(GsonUtil.gson().toJson(provider.getNN(new GeoPoint(30, 60), 20, 2)));
    }
}
