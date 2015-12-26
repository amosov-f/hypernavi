package ru.hypernavi.core.database.provider.mongo;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


import com.google.inject.Inject;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.Document;
import org.bson.types.ObjectId;
import ru.hypernavi.commons.Index;
import ru.hypernavi.commons.Site;
import ru.hypernavi.core.geoindex.GeoIndex;
import ru.hypernavi.util.GeoPoint;
import ru.hypernavi.util.stream.MoreStreamSupport;

/**
 * Created by amosov-f on 05.12.15.
 */
public final class SiteMongoProvider extends MongoProvider<Site> implements GeoIndex<Site> {
    private static final Log LOG = LogFactory.getLog(SiteMongoProvider.class);

    @Inject
    public SiteMongoProvider(@NotNull final MongoDatabase db) {
        super(db, "site");
        coll.createIndex(new Document("position.location", "2dsphere"));
    }

    @Nullable
    @Override
    public Site get(@NotNull final String id) {
        if (!ObjectId.isValid(id)) {
            LOG.warn(id + " is invalid mongo id");
            return null;
        }
        return site(coll.find(toDoc(id)).iterator().tryNext()).get();
    }

    @NotNull
    @Override
    public String add(@NotNull final Site site) {
        final Document doc = toDoc(site);
        coll.insertOne(doc);
        return doc.getObjectId("_id").toString();
    }

    @Nullable
    @Override
    public Site remove(@NotNull final String id) {
        return Optional.ofNullable(site(coll.findOneAndDelete(toDoc(id)))).map(Index::get).orElse(null);
    }

    @Override
    public void put(@NotNull final String id, @NotNull final Site site) {
        coll.findOneAndUpdate(toDoc(id), new Document("$set", toDoc(site)));
    }

    @NotNull
    @Override
    public List<Index<? extends Site>> getNN(@NotNull final GeoPoint location, final int offset, final int count) {
        final Document near = new Document("$near", new Document().append("$geometry", toDoc(location)));
        return MoreStreamSupport.stream(coll.find(new Document("position.location", near)))
                .skip(offset)
                .limit(count)
                .map(SiteMongoProvider::site)
                .collect(Collectors.toList());
    }

    @Nullable
    private static Index<Site> site(@Nullable final Document doc) {
        return doc != null ? Index.of(id(doc), fromDoc(doc, Site.class)) : null;
    }
}
