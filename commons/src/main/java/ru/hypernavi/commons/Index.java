package ru.hypernavi.commons;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.Supplier;


import com.google.gson.*;
import ru.hypernavi.util.json.GsonUtils;

/**
 * Created by amosov-f on 19.12.15.
 */
public final class Index<T> implements Supplier<T> {
    static {
        GsonUtils.registerTypeAdapter(Index.class, new Serializer());
        GsonUtils.registerTypeAdapter(Index.class, new Deserializer());
    }

    @NotNull
    private final String id;
    @NotNull
    private final T obj;

    private Index(@NotNull final String id, @NotNull final T obj) {
        this.id = id;
        this.obj = obj;
    }

    @NotNull
    public static <T> Index<T> of(@NotNull final String id, @NotNull final T obj) {
        return new Index<>(id, obj);
    }

    @NotNull
    public String getId() {
        return id;
    }

    @NotNull
    @Override
    public T get() {
        return obj;
    }

    private static class Serializer implements JsonSerializer<Index<?>> {
        @NotNull
        @Override
        public JsonElement serialize(@NotNull final Index<?> index,
                                     @NotNull final Type indexType,
                                     @NotNull final JsonSerializationContext context) {
            final JsonElement json = context.serialize(index.get(), ((ParameterizedType) indexType).getActualTypeArguments()[0]);
            json.getAsJsonObject().addProperty("id", index.getId());
            return json;
        }
    }

    private static class Deserializer implements JsonDeserializer<Index<?>> {
        @NotNull
        @Override
        public Index<?> deserialize(@NotNull final JsonElement json,
                                    @NotNull final Type indexType,
                                    @NotNull final JsonDeserializationContext context) throws JsonParseException
        {
            final Object indexObj = context.deserialize(json, ((ParameterizedType) indexType).getActualTypeArguments()[0]);
            return of(json.getAsJsonObject().get("id").getAsString(), indexObj);
        }
    }
}
