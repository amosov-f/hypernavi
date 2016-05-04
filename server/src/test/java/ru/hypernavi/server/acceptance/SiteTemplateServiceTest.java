package ru.hypernavi.server.acceptance;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import ru.hypernavi.server.HyperNaviServerRunner;

import static org.junit.Assert.assertEquals;

/**
 * Created by amosov-f on 03.01.16.
 */
@RunWith(HyperNaviServerRunner.class)
public final class SiteTemplateServiceTest extends AcceptanceTest {
    @Test
    public void testHtmlResponse() throws IOException {
        final String site = IOUtils.toString(getClass().getResourceAsStream("/site.json"), StandardCharsets.UTF_8);
        final HttpPost req = new HttpPost("/admin/site");
        req.setEntity(new StringEntity(site));
        final HttpResponse resp = execute(req);
        assertEquals(HttpStatus.SC_OK, resp.getStatusLine().getStatusCode());
        assertEquals("text/html;charset=utf-8", resp.getEntity().getContentType().getValue());
    }
}
