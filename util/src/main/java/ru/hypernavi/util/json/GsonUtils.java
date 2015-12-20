package ru.hypernavi.util.json;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapterFactory;

/**
 * Created by amosov-f on 08.11.15.
 */
public enum GsonUtils {
    ;

    private static final List<GsonBuilderConsumer> settings = new ArrayList<GsonBuilderConsumer>() {{
        add(new GsonBuilderConsumer() {
            @Override
            public void accept(@NotNull final GsonBuilder builder) {
                builder.setVersion(1.0);
            }
        });
    }};

    public static void registerTypeAdapterFactory(@NotNull final TypeAdapterFactory factory) {
        settings.add(new GsonBuilderConsumer() {
            @Override
            public void accept(@NotNull final GsonBuilder builder) {
                builder.registerTypeAdapterFactory(factory);
            }
        });
    }

    public static void registerTypeAdapter(@NotNull final Type type, @NotNull final Object typeAdapter) {
        settings.add(new GsonBuilderConsumer() {
            @Override
            public void accept(@NotNull final GsonBuilder builder) {
                builder.registerTypeAdapter(type, typeAdapter);
            }
        });
    }

    @NotNull
    public static Gson gson() {
        return builder().create();
    }

    @NotNull
    public static GsonBuilder builder() {
        final GsonBuilder builder = new GsonBuilder();
        for (final GsonBuilderConsumer setting : settings) {
            setting.accept(builder);
        }
        return builder;
    }

    private interface GsonBuilderConsumer {
        void accept(@NotNull GsonBuilder builder);
    }
}
