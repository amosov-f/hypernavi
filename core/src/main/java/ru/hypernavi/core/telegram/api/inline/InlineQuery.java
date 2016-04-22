package ru.hypernavi.core.telegram.api.inline;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import ru.hypernavi.util.GeoPoint;

/**
 * User: amosov-f
 * Date: 22.04.16
 * Time: 23:06
 */
public final class InlineQuery {
    @NotNull
    private final String id;
    @Nullable
    private final GeoPoint location;
    @NotNull
    private final String query;
    @NotNull
    private final String offset;

    public InlineQuery(@NotNull final String id,
                       @Nullable final GeoPoint location,
                       @NotNull final String query,
                       @NotNull final String offset)
    {
        this.id = id;
        this.location = location;
        this.query = query;
        this.offset = offset;
    }

    @NotNull
    public String getId() {
        return id;
    }

    @Nullable
    public GeoPoint getLocation() {
        return location;
    }

    @NotNull
    public String getQuery() {
        return query;
    }

    @NotNull
    public String getOffset() {
        return offset;
    }
}
