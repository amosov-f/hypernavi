package ru.hypernavi.server.acceptance;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by amosov-f on 03.01.16.
 */
public final class SiteTemplateServiceTest extends AcceptanceTest {
    @Test
    public void testHtmlResponse() throws IOException {
        final String site = IOUtils.toString(getClass().getResourceAsStream("/site.json"), StandardCharsets.UTF_8);
        final HttpResponse resp = execute("/admin/site?site=" + URLEncoder.encode(site, StandardCharsets.UTF_8.name()));
        assertEquals(HttpStatus.SC_OK, resp.getStatusLine().getStatusCode());
        assertEquals("text/html;charset=utf-8", resp.getEntity().getContentType().getValue());
    }
}
