package ru.hypernavi.commons;

import java.util.Arrays;


import org.junit.Assert;
import org.junit.Test;
import ru.hypernavi.util.ArrayGeoPoint;
import ru.hypernavi.util.GeoPointImpl;
import ru.hypernavi.util.json.GsonUtils;

/**
 * Created by amosov-f on 08.11.15.
 */
public final class SearchResponseTest {
    @Test
    public void testSerialization() throws Exception {
        final SearchResponse.Data data = new SearchResponse.Data(Arrays.asList(
                Index.of("1", new Site(new GeoObject("site", "site", new GeoPointImpl(30, 60)))),
                Index.of("2", new Supermarket(new GeoObject("hyper", "hyper", ArrayGeoPoint.of(30, 60)), Plan.EMPTY_ARRAY, "okey"))
        ));
        final String json = GsonUtils.gson().toJson(new SearchResponse(new SearchResponse.Meta(new GeoPointImpl(30, 60)), data));
        final SearchResponse response = GsonUtils.gson().fromJson(json, SearchResponse.class);
        Assert.assertTrue(response.getData().getSites().get(1).get() instanceof Supermarket);
    }
}