package ru.hypernavi.server.acceptance;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import ru.hypernavi.commons.Dimension;
import ru.hypernavi.server.HyperNaviServerRunner;
import ru.hypernavi.server.servlet.admin.ImageSizeService;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Created by amosov-f on 23.10.16
 */
@RunWith(HyperNaviServerRunner.class)
public final class ImageSizeServiceTest extends AcceptanceTest {
    @Test
    public void imageSizeHandlerMustReturnImageDimensionAndSizeInBytes() throws UnsupportedEncodingException {
        final String imageLink = "http://hypernavi.net/img/4A1D07D7CF511735F7683FB681EA52DA.jpg";
        final String linkParam = URLEncoder.encode(imageLink, StandardCharsets.UTF_8.name());
        final String url = "/admin/image/size?link=" + linkParam;
        final ImageSizeService.ImageSize size = execute(url, ImageSizeService.ImageSize.class);
        Assert.assertEquals(Dimension.of(1251, 922), size.dimension);
        Assert.assertEquals(98709, size.fileSize);
    }
}
