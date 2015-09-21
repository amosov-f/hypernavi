package ru.hypernavi.server.acceptance;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;


import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import ru.hypernavi.commons.Hypermarket;
import ru.hypernavi.commons.InfoResponse;
import ru.hypernavi.commons.InfoResponceSerializer;
import ru.hypernavi.server.HyperNaviServerRunner;

import static org.junit.Assert.assertEquals;

/**
 * Created by Константин on 22.08.2015.
 */

@RunWith(HyperNaviServerRunner.class)
public class ClientBehaviorTest extends AcceptanceTest {
    @Test
    public void testSchemaInfoOkStatus() {
        final double lon = 30;
        final double lat = 60;
        final HttpResponse resp = execute("/schemainfo?lon=" + lon + "&lat=" + lat);
        assertEquals(HttpStatus.SC_OK, resp.getStatusLine().getStatusCode());
    }

    @Test
    public void testHypermarketSchemaSend() {
        final double lon = 30;
        final double lat = 60;
        HttpResponse resp = execute("/schemainfo?lon=" + lon + "&lat=" + lat);
        assertEquals(HttpStatus.SC_OK, resp.getStatusLine().getStatusCode());
        String pathSchema = null;
        try {
            final String json = IOUtils.toString(resp.getEntity().getContent());
            final JSONObject obj = new JSONObject(json);

            final InfoResponse inforesp = InfoResponceSerializer.deserialize(obj);
            if (inforesp == null || inforesp.getClosestMarkets() == null || inforesp.getClosestMarkets().size() == 0) {
                Assert.fail("Wrong responce");
            }
            final Hypermarket market = inforesp.getClosestMarkets().get(0);
            pathSchema = market.getPath();
        } catch (IOException e) {
            Assert.fail("Can't take content from responce " + e.getMessage());
        } catch (JSONException e) {
            Assert.fail("Invalid json " + e.getMessage());
        }

        resp = execute(pathSchema);
        try {
            final byte[] image = IOUtils.toByteArray(resp.getEntity().getContent());
            final InputStream in = new ByteArrayInputStream(image);
            final BufferedImage bImageFromConvert = ImageIO.read(in);
            Assert.assertTrue(bImageFromConvert.getHeight() > 0);
        } catch (IOException e) {
            Assert.fail("Bad image come " + e.getMessage());
        }
    }


    @Test
    public void testImgNotFoundStatus() {
        final HttpResponse resp = execute("/img/not_found.jpg");
        assertEquals(HttpStatus.SC_NOT_FOUND, resp.getStatusLine().getStatusCode());
    }

    @Test
    public void testImgOkStatus() {
        final HttpResponse resp = execute("/img/4A1D07D7CF511735F7683FB681EA52DA.jpg");
        assertEquals(HttpStatus.SC_OK, resp.getStatusLine().getStatusCode());
    }


    @Test
    public void testRegisterBadRequiestStatus() {
        final HttpResponse resp = execute("/register/hypermarket");
        assertEquals(HttpStatus.SC_BAD_REQUEST, resp.getStatusLine().getStatusCode());
    }

    @Test
    public void testHypermarketOkStatus() {
        final HttpResponse resp = execute("/hypermarket");
        assertEquals(HttpStatus.SC_OK, resp.getStatusLine().getStatusCode());
    }
}
