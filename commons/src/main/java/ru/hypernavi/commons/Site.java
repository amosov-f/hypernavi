package ru.hypernavi.commons;

import org.jetbrains.annotations.NotNull;


import net.jcip.annotations.Immutable;
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
                .registerSubtype(Image.class, "image")
                .registerSubtype(Plan.class, "plan"));
    }

    @NotNull
    private final GeoObject position;
    @NotNull
    private final Hint[] hints;

    public Site(@NotNull final GeoObject position, @NotNull final Hint... hints) {
        this.position = position;
        this.hints = hints;
    }

    @NotNull
    public final GeoObject getPosition() {
        return position;
    }

    @NotNull
    public final Hint[] getHints() {
        return hints;
    }

    @NotNull
    @Override
    public GeoPoint getLocation() {
        return position.getLocation();
    }
}
