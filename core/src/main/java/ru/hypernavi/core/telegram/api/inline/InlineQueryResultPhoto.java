package ru.hypernavi.core.telegram.api.inline;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: amosov-f
 * Date: 22.04.16
 * Time: 23:21
 */
public final class InlineQueryResultPhoto extends InlineQueryResult {
    @NotNull
    private final String id;
    @NotNull
    private final String photoUrl;
    @NotNull
    private final String thumbUrl;
    @Nullable
    private final String caption;

    public InlineQueryResultPhoto(@NotNull final String id,
                                  @NotNull final String photoUrl,
                                  @NotNull final String thumbUrl,
                                  @Nullable final String caption) {
        super("photo");
        this.id = id;
        this.photoUrl = photoUrl;
        this.thumbUrl = thumbUrl;
        this.caption = caption;
    }
}
