package ru.hypernavi.commons;

import org.jetbrains.annotations.NotNull;

import java.util.List;


import net.jcip.annotations.Immutable;
import ru.hypernavi.util.GeoPoint;
import ru.hypernavi.util.json.GsonUtils;
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
            GsonUtils.registerTypeAdapterFactory(RuntimeTypeAdapterFactory.of(Site.class, "type")
                    .registerSubtype(Site.class, "site")
                    .registerSubtype(Supermarket.class, "supermarket"));
        }

        @NotNull
        private final List<Index<? extends Site>> sites;

        public Data(@NotNull final List<Index<? extends Site>> sites) {
            this.sites = sites;
        }

        @NotNull
        public List<Index<? extends Site>> getSites() {
            return sites;
        }
    }
}
