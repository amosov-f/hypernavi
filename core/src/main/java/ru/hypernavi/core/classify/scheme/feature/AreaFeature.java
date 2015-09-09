package ru.hypernavi.core.classify.scheme.feature;

import org.jetbrains.annotations.NotNull;


import ru.hypernavi.core.classify.scheme.Picture;
import ru.hypernavi.ml.factor.CachedFactor;

/**
 * Created by Константин on 09.09.2015.
 */
public class AreaFeature extends CachedFactor<Picture> {

    public AreaFeature(final boolean cached) {
        super("area", cached);
    }

    @Override
    public double applyCachedDouble(@NotNull final Picture value) {
        return value.getImage().getHeight() * value.getImage().getWidth();
    }
}
