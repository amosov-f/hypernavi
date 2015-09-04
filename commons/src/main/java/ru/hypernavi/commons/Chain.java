package ru.hypernavi.commons;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by amosov-f on 05.09.15.
 */
public enum Chain {
    OKEY, AUCHAN, LEROYMERLIN, STROYINLOCK, CASTORAMA, MAXIDOM;

    @Nullable
    public static Chain parse(@NotNull final String name) {
        for (final Chain chain : values()) {
            if (chain.name().equalsIgnoreCase(name)) {
                return chain;
            }
        }
        return null;
    }
}
