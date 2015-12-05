package ru.hypernavi.commons;

import org.junit.Assert;
import org.junit.Test;
import ru.hypernavi.util.GeoPoint;
import ru.hypernavi.util.json.GsonUtil;

/**
 * Created by amosov-f on 08.11.15.
 */
public final class SearchResponseTest {
    @Test
    public void testSerialization() throws Exception {
        final SearchResponse.Data data = new SearchResponse.Data(
                new Site(new GeoObject("site", "site", new GeoPoint(30, 60))),
                new Supermarket(new GeoObject("hyper", "hyper", new GeoPoint(30, 60)), Plan.EMPTY_ARRAY, "okey")
        );
        final String json = GsonUtil.gson().toJson(new SearchResponse(new SearchResponse.Meta(new GeoPoint(30, 60)), data));
        final SearchResponse response = GsonUtil.gson().fromJson(json, SearchResponse.class);
        Assert.assertTrue(response.getData().getSites()[1] instanceof Supermarket);
    }
}