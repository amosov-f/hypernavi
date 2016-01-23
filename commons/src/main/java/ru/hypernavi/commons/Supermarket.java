package ru.hypernavi.commons;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import net.jcip.annotations.Immutable;
import ru.hypernavi.commons.hint.Plan;

/**
 * TODO: rename to Hypermarket
 *
 * Created by amosov-f on 07.11.15.
 */
@Immutable
public final class Supermarket extends Site {
    @Nullable
    private final String chain;

    public Supermarket(@NotNull final GeoObject position, @NotNull final Plan[] plans, @Nullable final String chain) {
        super(position, plans);
        this.chain = chain;
    }

    @Nullable
    public String getChain() {
        return chain;
    }
}
