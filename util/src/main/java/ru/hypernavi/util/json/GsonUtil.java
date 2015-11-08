package ru.hypernavi.util.json;

import org.jetbrains.annotations.NotNull;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapterFactory;

/**
 * Created by amosov-f on 08.11.15.
 */
public enum GsonUtil {
    ;

    private static final GsonBuilder GSON_BUILDER = new GsonBuilder().setPrettyPrinting();

    public static void registerTypeAdapterFactory(@NotNull final TypeAdapterFactory factory) {
        GSON_BUILDER.registerTypeAdapterFactory(factory);
    }

    @NotNull
    public static Gson gson() {
        return GSON_BUILDER.create();
    }
}
