package ru.hypernavi.server.acceptance;

import java.io.IOException;


import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.junit.Test;

/**
 * User: amosov-f
 * Date: 11.05.16
 * Time: 23:09
 */
public class ValidatePointsServiceTest extends AcceptanceTest {
    @Test
    public void regressDistances() throws IOException {
        final String points = fromClasspath("/validate/points.json").toString();
        final HttpPost req = new HttpPost("/admin/validate");
        req.setEntity(new StringEntity(points));
        final HttpResponse resp = execute(req);
        assertJsonEquals("/validate/distances.json", IOUtils.toString(resp.getEntity().getContent()));
    }
}
