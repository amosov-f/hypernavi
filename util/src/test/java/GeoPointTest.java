import org.junit.Assert;
import org.junit.Test;
import ru.hypernavi.util.GeoPoint;

/**
 * Created by Acer on 27.07.2015.
 */
public final class GeoPointTest {
    private static final double EPS = 0.1;

    @Test
    public void test() throws Exception {
        //test(60.0, 30.0, 59.85588055, 30.0, 16.71);
        test(60.0, 30.0, 60.0, 30.6098805, 33.93);
    }

    private void test(final double firstLatitude, final double firstLongitude, final double secondLatitude,
                      final double secondLongitude, final double actualDistance) {
        final GeoPoint firstGeoPoint = new GeoPoint(firstLatitude, firstLongitude);
        final GeoPoint secondGeoPoint = new GeoPoint(secondLatitude, secondLongitude);
        Assert.assertEquals(GeoPoint.distance(firstGeoPoint, secondGeoPoint), actualDistance, EPS);
    }
}
