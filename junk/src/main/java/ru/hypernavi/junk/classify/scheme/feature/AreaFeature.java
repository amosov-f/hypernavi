package ru.hypernavi.junk.classify.scheme.feature;

import ru.hypernavi.junk.classify.scheme.Picture;
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
