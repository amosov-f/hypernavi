package ru.hypernavi.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Created by amosov-f on 18.10.15.
 */
public enum JsonUtil {
    ;

    @Nullable
    public static JsonObject asJsonObjectIfExists(@NotNull final JsonObject json, @NotNull final String memberName) {
        final JsonElement el = json.get(memberName);
        return el != null && el.isJsonObject() ? el.getAsJsonObject() : null;
    }

    @NotNull
    public static JsonObject asJsonObject(@NotNull final JsonArray array, final int i) {
        return array.get(i).getAsJsonObject();
    }

    @NotNull
    public static Iterable<JsonObject> asJsonObjectArray(@NotNull final JsonObject json, @NotNull final String memberName) {
        final Iterator<JsonElement> it = json.getAsJsonArray(memberName).iterator();
        return new Iterable<JsonObject>() {
            @Override
            public Iterator<JsonObject> iterator() {
                return new Iterator<JsonObject>() {
                    @Override
                    public boolean hasNext() {
                        return it.hasNext();
                    }

                    @Override
                    public JsonObject next() {
                        return it.next().getAsJsonObject();
                    }
                };
            }
        };
    }
}
