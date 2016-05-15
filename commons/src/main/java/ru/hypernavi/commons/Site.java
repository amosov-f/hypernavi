package ru.hypernavi.commons;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import net.jcip.annotations.Immutable;
import ru.hypernavi.commons.hint.Hint;
import ru.hypernavi.commons.hint.Picture;
import ru.hypernavi.commons.hint.Plan;
import ru.hypernavi.util.GeoPoint;
import ru.hypernavi.util.json.GsonUtils;
import ru.hypernavi.util.json.RuntimeTypeAdapterFactory;

/**
 * Created by amosov-f on 07.11.15.
 */
@Immutable
public class Site implements Positioned {
    static {
        GsonUtils.registerTypeAdapterFactory(RuntimeTypeAdapterFactory.of(Hint.class, "type")
                .registerSubtype(Picture.class, Picture.TYPE.name().toLowerCase())
                .registerSubtype(Plan.class, Plan.TYPE.name().toLowerCase()));
    }

    @NotNull
    private final GeoObject place;
    @NotNull
    private final Hint[] hints;

    public Site(@NotNull final GeoObject place, @NotNull final Hint... hints) {
        this(place, hints, null);
    }

    public Site(@NotNull final GeoObject place, @NotNull final Hint[] hints, @Nullable final Integer authorUid) {
        this.place = place;
        this.hints = hints;
    }

    @NotNull
    public final GeoObject getPlace() {
        return place;
    }

    @NotNull
    public final Hint[] getHints() {
        return hints;
    }

    @NotNull
    @Override
    public GeoPoint getLocation() {
        return place.getLocation();
    }

    public void setIfNotPresent(final int authorUid) {
        for (final Hint hint : getHints()) {
            if (hint.getAuthorUid() == null) {
                hint.setAuthorUid(authorUid);
            }
        }
    }
}
