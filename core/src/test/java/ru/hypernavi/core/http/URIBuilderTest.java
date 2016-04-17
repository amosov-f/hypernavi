package ru.hypernavi.core.http;

import org.junit.Assert;
import org.junit.Test;

/**
 * User: amosov-f
 * Date: 17.04.16
 * Time: 16:33
 */
public class URIBuilderTest {
    @Test
    public void testParamRemoving() {
        final URIBuilder builder = new URIBuilder("http://hypernavi.net/admin?uid=1");
        builder.remove("uid");
        Assert.assertEquals("http://hypernavi.net/admin", builder.build().toString());
    }
}