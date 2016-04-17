package ru.hypernavi.server.acceptance;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import ru.hypernavi.server.HyperNaviServerRunner;

import static org.junit.Assert.assertEquals;

/**
 * User: amosov-f
 * Date: 17.04.16
 * Time: 15:30
 */
@RunWith(HyperNaviServerRunner.class)
public final class SearchServiceTest extends AcceptanceTest {
    @Test
    public void testJsonResponse() throws Exception {
        final HttpResponse resp = execute("/search?lon=30&lat=60");
        assertEquals(HttpStatus.SC_OK, resp.getStatusLine().getStatusCode());
        assertEquals("application/json;charset=utf-8", resp.getEntity().getContentType().getValue());
    }
}
