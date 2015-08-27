package ru.hypernavi.server.acceptance;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import ru.hypernavi.server.HyperNaviServerRunner;

import static org.junit.Assert.assertEquals;

/**
 * Created by amosov-f on 22.08.15.
 */
@RunWith(HyperNaviServerRunner.class)
public final class AdminInfoSanityTest extends AcceptanceTest {
    @Test(timeout = 1000)
    public void testOkStatus() {
        final HttpResponse resp = execute("/admin/info");
        assertEquals(HttpStatus.SC_OK, resp.getStatusLine().getStatusCode());
    }
}
