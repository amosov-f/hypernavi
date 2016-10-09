package ru.hypernavi.junk.classify.scheme.feature;

import org.jetbrains.annotations.NotNull;
import ru.hypernavi.junk.classify.scheme.Picture;
import ru.hypernavi.ml.factor.BinaryFactor;

/**
 * Created by amosov-f on 05.09.15.
 */
public class AuchanHostnameFeature extends BinaryFactor<Picture> {
    public AuchanHostnameFeature() {
        super("auchan_hostname");
    }

    @Override
    public boolean test(@NotNull final Picture picture) {
        return picture.getUrl().getHost().contains("auchan");
    }
}
