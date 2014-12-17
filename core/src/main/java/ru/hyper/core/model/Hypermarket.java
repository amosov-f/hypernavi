package ru.hyper.core.model;

import org.jetbrains.annotations.NotNull;

/**
 * User: amosov-f
 * Date: 26.11.14
 * Time: 23:45
 */
public class Hypermarket {
    private enum Chain {
        OKEY, KARUSEL, LENTA, ASHAN
    }

    @NotNull
    private final Chain chain;
    @NotNull
    private final Scheme scheme;

    public Hypermarket(@NotNull final Chain chain, @NotNull Scheme scheme) {
        this.chain = chain;
        this.scheme = scheme;
    }
}
