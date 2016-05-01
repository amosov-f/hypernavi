package ru.hypernavi.server.acceptance;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import ru.hypernavi.server.HyperNaviServerRunner;

/**
 * User: amosov-f
 * Date: 02.05.16
 * Time: 1:21
 */
@RunWith(HyperNaviServerRunner.class)
public final class TelegramWebhookServiceTest extends AcceptanceTest {
    @Test
    public void webhookHandleResponseStatusMustBe200() throws Exception {
        final HttpPost req = new HttpPost("/telegram/139192271:AAGD6kiaoiiJEBxnquWOdu2WTVLisAqAWPE");
        req.setEntity(new InputStreamEntity(getClass().getResourceAsStream("/telegram/webhook/location.json"), ContentType.APPLICATION_JSON));
        final HttpResponse resp = execute(req);
        Assert.assertEquals(HttpStatus.SC_OK, resp.getStatusLine().getStatusCode());
    }
}
