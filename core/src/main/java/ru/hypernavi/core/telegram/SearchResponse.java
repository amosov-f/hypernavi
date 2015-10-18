package ru.hypernavi.core.telegram;

import org.jetbrains.annotations.NotNull;


import ru.hypernavi.commons.Hypermarket;

/**
 * Created by amosov-f on 18.10.15.
 */
public final class SearchResponse {
    @NotNull
    private final Data data;

    public SearchResponse(@NotNull final Data data) {
        this.data = data;
    }

    @NotNull
    public Data getData() {
        return data;
    }

    public static final class Data {
        @NotNull
        private final Hypermarket[] hypermarkets;

        public Data(@NotNull final Hypermarket... hypermarkets) {
            this.hypermarkets = hypermarkets;
        }

        @NotNull
        public Hypermarket[] getHypermarkets() {
            return hypermarkets;
        }
    }
}
