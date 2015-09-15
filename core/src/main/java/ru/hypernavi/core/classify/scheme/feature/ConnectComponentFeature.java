package ru.hypernavi.core.classify.scheme.feature;

import ru.hypernavi.core.classify.scheme.Picture;
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
