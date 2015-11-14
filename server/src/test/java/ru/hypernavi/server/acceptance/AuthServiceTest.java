package ru.hypernavi.server.acceptance;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import ru.hypernavi.server.HyperNaviServerRunner;

import static org.junit.Assert.assertEquals;

/**
 * Created by amosov-f on 14.11.15.
 */
@RunWith(HyperNaviServerRunner.class)
public final class AuthServiceTest extends AcceptanceTest {
    @Test
    public void testResponseHeaders() {
        final HttpResponse resp = execute("/auth?url=/admin");
        assertEquals(HttpStatus.SC_OK, resp.getStatusLine().getStatusCode());
        assertEquals("text/html;charset=utf-8", resp.getEntity().getContentType().getValue());
    }
}
