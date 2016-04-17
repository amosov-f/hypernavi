package ru.hypernavi.core.webutil.yandex;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.Optional;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import org.apache.http.client.methods.HttpGet;
import ru.hypernavi.commons.GeoObject;
import ru.hypernavi.core.http.HyperHttpClient;
import ru.hypernavi.core.http.URIBuilder;
import ru.hypernavi.util.ArrayGeoPoint;
import ru.hypernavi.util.GeoPoint;
import ru.hypernavi.util.json.GsonUtils;
import ru.hypernavi.util.json.MoreGsonUtils;

/**
 * Created by amosov-f on 11.01.16.
 */
public final class MapsSearcher {
    private static final String API_KEY = "72df8f2d-f9c5-431d-8d14-c22f233f34c2";

    @NotNull
    private final HyperHttpClient httpClient;

    @Inject
    public MapsSearcher(@NotNull final HyperHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Nullable
    public GeoObject search(@NotNull final String text, @Nullable final GeoPoint center) {
        final URI uri = new URIBuilder("https://search-maps.yandex.ru/v1/")
                .add("apikey", API_KEY)
                .add("text", text)
                .add("lang", "ru_RU")
                .addIfNotNull("ll", Optional.ofNullable(center).map(MapsSearcher::ll).orElse(null))
                .add("results", 1)
                .build();
        final JsonObject response = httpClient.executeText(new HttpGet(uri), MoreGsonUtils.parser());
        if (response == null) {
            return null;
        }
        final JsonArray features = response.getAsJsonArray("features");
        if (features.size() == 0) {
            return null;
        }
        final JsonObject fearure = features.get(0).getAsJsonObject();
        final JsonObject properties = fearure.getAsJsonObject("properties");
        final String name = properties.get("name").getAsString();
        final String description = properties.get("description").getAsString();
        final GeoPoint location = GsonUtils.gson().fromJson(fearure.get("geometry"), ArrayGeoPoint.class);
        return new GeoObject(name, description, location);
    }

    @NotNull
    private static String ll(@NotNull final GeoPoint location) {
        return location.getLongitude() + "," + location.getLatitude();
    }
}
