package ru.hypernavi.core.database.provider.mongo;

import org.jetbrains.annotations.NotNull;


import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import ru.hypernavi.core.database.provider.DatabaseProvider;
import ru.hypernavi.util.json.GsonUtils;

/**
 * Created by amosov-f on 05.12.15.
 */
public abstract class MongoProvider<T> implements DatabaseProvider<T> {
    @NotNull
    protected final MongoCollection<Document> coll;

    protected MongoProvider(@NotNull final MongoDatabase db, @NotNull final String collName) {
        coll = db.getCollection(collName);
    }

    @NotNull
    protected static Document toDoc(@NotNull final Object obj) {
        return Document.parse(GsonUtils.gson().toJson(obj));
    }

    @NotNull
    protected static <T> T fromDoc(@NotNull final Document doc, @NotNull final Class<T> clazz) {
        return GsonUtils.gson().fromJson(doc.toJson(), clazz);
    }

    @NotNull
    protected static String id(@NotNull final Document doc) {
        return doc.getObjectId("_id").toString();
    }

    @NotNull
    protected static Document toDoc(@NotNull final String id) {
        return new Document("_id", new ObjectId(id));
    }
}
