package ru.hypernavi.core.classify.scheme.feature;

import ru.hypernavi.core.classify.scheme.Picture;
import ru.hypernavi.ml.factor.Factor;

/**
 * Created by Константин on 10.09.2015.
 */
public class DiagonalFeature extends Factor<Picture> {
    public DiagonalFeature() {
        super("diagonal");
    }

    @Override
    public double applyAsDouble(final Picture value) {
        final double w = value.getImage().getWidth();
        final double h = value.getImage().getHeight();
        return w / h;
    }
}
