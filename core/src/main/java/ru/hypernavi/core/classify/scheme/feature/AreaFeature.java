package ru.hypernavi.core.classify.scheme.feature;

import ru.hypernavi.core.classify.scheme.Picture;
import ru.hypernavi.ml.factor.Factor;

/**
 * Created by Константин on 09.09.2015.
 */
public class AreaFeature extends Factor<Picture> {

    public AreaFeature() {
        super("area");
    }


    @Override
    public double applyAsDouble(final Picture value) {
        return value.getImage().getHeight() * value.getImage().getWidth();
    }
}
