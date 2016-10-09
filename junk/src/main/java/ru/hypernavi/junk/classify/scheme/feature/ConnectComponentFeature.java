package ru.hypernavi.junk.classify.scheme.feature;

import ru.hypernavi.junk.classify.scheme.Picture;
import ru.hypernavi.ml.factor.Factor;

/**
 * Created by Константин on 10.09.2015.
 */
public class ConnectComponentFeature extends Factor<Picture> {
    protected ConnectComponentFeature() {
        super("connected_component");
    }

    @Override
    public double applyAsDouble(final Picture value) {


        return 0;
    }
}
