package ru.hypernavi.core.database.provider.mongo;

import org.jetbrains.annotations.NotNull;


import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import ru.hypernavi.core.database.provider.DatabaseProvider;
import ru.hypernavi.util.json.GsonUtil;

/**
 * Created by amosov-f on 05.12.15.
 */
public abstract class MongoProvider<T> implements DatabaseProvider<T> {
    protected static final Gson GSON = GsonUtil.gson();

    @NotNull
    protected final MongoCollection<Document> coll;

    protected MongoProvider(@NotNull final MongoDatabase db, @NotNull final String collName) {
        coll = db.getCollection(collName);
    }

    @NotNull
    protected static Document toDoc(@NotNull final Object obj) {
        return Document.parse(GSON.toJson(obj));
    }

    @NotNull
    protected static <T> T fromDoc(@NotNull final Document doc, @NotNull final Class<T> clazz) {
        return GSON.fromJson(doc.toJson(), clazz);
    }
}
