package ru.hypernavi.util.json;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;


import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Created by amosov-f on 20.12.15.
 */
public enum MoreGsonUtils {
    ;

    @NotNull
    public static Function<String, JsonObject> parser() {
        return parser(JsonObject.class);
    }

    @NotNull
    public static <T> Function<String, T> parser(@NotNull final Class<T> clazz) {
        return parser(GsonUtils.gson(), clazz);
    }

    @NotNull
    public static <T> Function<String, T> parser(@NotNull final Gson gson, @NotNull final Class<T> clazz) {
        return json -> gson.fromJson(json, clazz);
    }
}
