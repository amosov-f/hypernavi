package ru.hypernavi.server.acceptance;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import ru.hypernavi.server.HyperNaviServerRunner;

import static org.junit.Assert.assertEquals;

/**
 * Created by amosov-f on 14.11.15.
 */
@RunWith(HyperNaviServerRunner.class)
public final class AdminServiceTest extends AcceptanceTest {
    @Test
    @Ignore
    public void testUnauthorizedRequest() {
        final HttpResponse resp = execute("/admin");
        assertEquals(HttpStatus.SC_MOVED_TEMPORARILY, resp.getStatusLine().getStatusCode());
        assertEquals("http://localhost:8081/auth?url=/admin", resp.getFirstHeader(HttpHeaders.LOCATION).getValue());
    }
}
