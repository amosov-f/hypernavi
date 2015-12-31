package ru.hypernavi.util.json;

import org.jetbrains.annotations.NotNull;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import ru.hypernavi.util.function.IOFunction;

/**
 * Created by amosov-f on 20.12.15.
 */
public enum MoreGsonUtils {
    ;

    @NotNull
    public static IOFunction<String, JsonObject> parser() {
        return parser(JsonObject.class);
    }

    @NotNull
    public static <T> IOFunction<String, T> parser(@NotNull final Class<T> clazz) {
        return parser(GsonUtils.gson(), clazz);
    }

    @NotNull
    public static <T> IOFunction<String, T> parser(@NotNull final Gson gson, @NotNull final Class<T> clazz) {
        return json -> gson.fromJson(json, clazz);
    }
}
