package ru.hypernavi.junk.classify.scheme.feature;

import org.jetbrains.annotations.NotNull;


import ru.hypernavi.junk.classify.scheme.Picture;
import ru.hypernavi.ml.factor.BinaryFactor;

/**
 * Created by amosov-f on 03.09.15.
 */
public final class OkeyHostnameFeature extends BinaryFactor<Picture> {
    public OkeyHostnameFeature() {
        super("okey_hostname");
    }

    @Override
    public boolean test(@NotNull final Picture picture) {
        return picture.getUrl().getHost().contains("okmarket");
    }
}
