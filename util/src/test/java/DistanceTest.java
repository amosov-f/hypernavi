import static org.junit.Assert.assertEquals;
import org.junit.Test;
import ru.hypernavi.util.GeoPointImpl;

/**
 * Created by Acer on 27.07.2015.
 */
public final class DistanceTest {

    @Test
    public void test() throws Exception {
        test(46.688094, 30.989175, 46.344361, 30.578533, 49.53);
        test(60.004703, 30.022456, 59.808408, 30.402464, 30.5);
        test(59.908608, 29.756953, 59.817917, 30.150514, 24.25);
        test(60.0, 30.0, 60.0, 30.6098805, 33.93);
    }

    private static void test(final double firstLatitude, final double firstLongitude, final double secondLatitude,
                      final double secondLongitude, final double actualDistance)
    {
        final GeoPointImpl firstGeoPoint = new GeoPointImpl(firstLongitude, firstLatitude);
        final GeoPointImpl secondGeoPoint = new GeoPointImpl(secondLongitude, secondLatitude);
        assertEquals(GeoPointImpl.distance(firstGeoPoint, secondGeoPoint), actualDistance, actualDistance * 4.0E-3);
    }
}
