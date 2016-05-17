package ru.hypernavi.core.webutil.yandex;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.jetbrains.annotations.NotNull;
import ru.hypernavi.core.auth.VkUser;
import ru.hypernavi.core.http.HyperHttpClient;
import ru.hypernavi.core.http.URIBuilder;
import ru.hypernavi.util.json.GsonUtils;
import ru.hypernavi.util.stream.MoreStreamSupport;

import java.net.URI;

/**
 * Created by amosov-f on 17.05.16.
 */
public final class VkApi {
    @NotNull
    private final HyperHttpClient httpClient;

    @Inject
    public VkApi(@NotNull final HyperHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @NotNull
    public VkUser[] getUser(final int... uids) {
        if (uids.length == 0) {
            return VkUser.EMPTY_ARRAY;
        }
        final URI uri = new URIBuilder("https://api.vk.com/method/users.get")
                .add("user_ids", StringUtils.join(uids, ','))
                .add("fields", "photo_100,photo_400_orig")
                .build();
        final HttpGet req = new HttpGet(uri);
        final JsonObject resp = httpClient.execute(req, JsonObject.class, GsonUtils.gson());
        if (resp == null) {
            return VkUser.EMPTY_ARRAY;
        }
        return MoreStreamSupport.stream(resp.get("response").getAsJsonArray())
                .map(JsonElement::getAsJsonObject)
                .map(this::parse)
                .toArray(VkUser[]::new);
    }

    @NotNull
    private VkUser parse(@NotNull final JsonObject json) {
        return new VkUser.Builder(json.get("uid").getAsInt())
                .firstName(json.get("first_name").getAsString())
                .lastName(json.get("last_name").getAsString())
                .photo(json.get("photo_400_orig").getAsString())
                .photoRec(json.get("photo_100").getAsString())
                .build();
    }
}
