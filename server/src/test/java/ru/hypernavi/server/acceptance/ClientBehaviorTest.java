package ru.hypernavi.server.acceptance;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import ru.hypernavi.server.HyperNaviServerRunner;

import static org.junit.Assert.assertEquals;

/**
 * Created by Константин on 22.08.2015.
 */

@RunWith(HyperNaviServerRunner.class)
public class ClientBehaviorTest extends AcceptanceTest {
    @Test
    public void testOkStatus() {
        final double lon = 30;
        final double lat = 60;
        final HttpResponse resp = execute("/schemainfo?lon="+lon+"&lat="+lat);
        assertEquals(HttpStatus.SC_OK, resp.getStatusLine().getStatusCode());
    }
}
