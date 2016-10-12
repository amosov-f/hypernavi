package ru.hypernavi.server.acceptance;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import ru.hypernavi.commons.Image;
import ru.hypernavi.server.HyperNaviServerRunner;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * User: amosov-f
 * Date: 14.05.16
 * Time: 0:58
 */
@RunWith(HyperNaviServerRunner.class)
public final class DuplicateImageServiceTest extends AcceptanceTest {
    @Test
    public void backupMustReturnDuplicatedImageObject() throws Exception {
        final String path = "/img/4A1D07D7CF511735F7683FB681EA52DA.jpg";
        final String imageLink = "http://hypernavi.net" + path;
        final String linkParam = URLEncoder.encode(imageLink, StandardCharsets.UTF_8.name());
        final Image image = execute("/admin/image/duplicate?link=" + linkParam, Image.class);
        Assert.assertEquals(imageLink, image.getLink());
        Assert.assertEquals("http://localhost:8081" + path, image.getDuplicates()[0].getLink());
        Assert.assertEquals("http://localhost:8081/thumb" + path, image.getThumbLink());
        Assert.assertTrue(new File("./data" + path).exists());
        Assert.assertTrue(new File("./data/thumb" + path).exists());
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.deleteDirectory(new File("./data/img"));
        FileUtils.deleteDirectory(new File("./data/thumb"));
    }
}
