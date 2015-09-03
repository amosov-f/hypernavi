package ru.hypernavi.core.classify.scheme.okey;

import ru.hypernavi.core.classify.scheme.Picture;
import ru.hypernavi.ml.factor.BinaryFactor;

/**
 * Created by amosov-f on 03.09.15.
 */
public final class HostnameFeature extends BinaryFactor<Picture> {
    public HostnameFeature() {
        super("okey_hostname");
    }

    @Override
    public boolean test(final Picture picture) {
        return picture.getUrl().getHost().contains("okmarket");
    }
}
