package ru.hypernavi.commons;

import org.jetbrains.annotations.NotNull;


import net.jcip.annotations.Immutable;
import ru.hypernavi.util.GeoPoint;
import ru.hypernavi.util.json.GsonUtil;
import ru.hypernavi.util.json.RuntimeTypeAdapterFactory;

/**
 * Created by amosov-f on 07.11.15.
 */
@Immutable
public final class SearchResponse {
    @NotNull
    private final Meta meta;
    @NotNull
    private final Data data;

    public SearchResponse(@NotNull final Meta meta, @NotNull final Data data) {
        this.meta = meta;
        this.data = data;
    }

    @NotNull
    public Meta getMeta() {
        return meta;
    }

    @NotNull
    public Data getData() {
        return data;
    }

    @Immutable
    public static final class Meta {
        @NotNull
        private final GeoPoint location;

        public Meta(@NotNull final GeoPoint location) {
            this.location = location;
        }

        @NotNull
        public GeoPoint getLocation() {
            return location;
        }
    }

    @Immutable
    public static final class Data {
        static {
            GsonUtil.registerTypeAdapterFactory(RuntimeTypeAdapterFactory.of(Site.class, "type")
                    .registerSubtype(Site.class, "site")
                    .registerSubtype(Supermarket.class, "supermarket"));
        }

        @NotNull
        private final Site[] sites;

        public Data(@NotNull final Site... sites) {
            this.sites = sites;
        }

        @NotNull
        public Site[] getSites() {
            return sites;
        }
    }
}
