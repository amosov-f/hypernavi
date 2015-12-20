package ru.hypernavi.core.webutil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import net.jcip.annotations.ThreadSafe;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.HttpGet;
import ru.hypernavi.commons.GeoObject;
import ru.hypernavi.core.http.HyperHttpClient;
import ru.hypernavi.core.http.URIBuilder;
import ru.hypernavi.util.ArrayGeoPoint;
import ru.hypernavi.util.json.MoreGsonUtils;

/**
 * Created by amosov-f on 07.11.15.
 */
@ThreadSafe
public final class GeoDecoder {
    private static final Log LOG = LogFactory.getLog(GeoDecoder.class);

    @NotNull
    private final HyperHttpClient httpClient;

    @Inject
    public GeoDecoder(@NotNull final HyperHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Nullable
    public GeoObject decode(@NotNull final String geocode) {
        final URI uri = new URIBuilder("https://geocode-maps.yandex.ru/1.x")
                .addParameter("geocode", geocode)
                .addParameter("kind", "house")
                .addParameter("results", 1)
                .addParameter("format", "json")
                .build();
        final JsonObject response = httpClient.execute(new HttpGet(uri), MoreGsonUtils.parser());
        if (response == null) {
            return null;
        }
        final JsonArray featureMember = response
                .getAsJsonObject("response")
                .getAsJsonObject("GeoObjectCollection")
                .getAsJsonArray("featureMember");
        if (featureMember.size() == 0) {
            return null;
        }
        final JsonObject geoObjectJson = featureMember.get(0).getAsJsonObject().getAsJsonObject("GeoObject");
        final String name = geoObjectJson.get("name").getAsString();
        final String description = geoObjectJson.get("description").getAsString();
        final String[] position = geoObjectJson.getAsJsonObject("Point").get("pos").getAsString().split("\\s+");
        return new GeoObject(name, description, ArrayGeoPoint.of(Double.parseDouble(position[0]), Double.parseDouble(position[1])));
    }
}
