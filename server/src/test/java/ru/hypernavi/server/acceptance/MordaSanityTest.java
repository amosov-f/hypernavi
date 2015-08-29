package ru.hypernavi.server.acceptance;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import ru.hypernavi.server.HyperNaviServerRunner;

import static org.junit.Assert.assertEquals;

/**
 * Created by amosov-f on 28.08.15.
 */
@RunWith(HyperNaviServerRunner.class)
public final class MordaSanityTest extends AcceptanceTest {
    @Test(timeout = 2000)
    public void testHtmlContentType() {
        final HttpResponse resp = execute("/");
        assertEquals(HttpStatus.SC_OK, resp.getStatusLine().getStatusCode());
        assertEquals("text/html;charset=utf-8", resp.getEntity().getContentType().getValue());
    }
}
