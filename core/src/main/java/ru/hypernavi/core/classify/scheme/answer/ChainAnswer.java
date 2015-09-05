package ru.hypernavi.core.classify.scheme.answer;

import org.jetbrains.annotations.NotNull;


import ru.hypernavi.core.classify.scheme.Picture;
import ru.hypernavi.ml.factor.ClassFactor;
import ru.hypernavi.util.EnumUtils;

/**
 * Created by amosov-f on 05.09.15.
 */
public final class ChainAnswer extends ClassFactor<Picture> {
    public ChainAnswer() {
        super("chain");
    }

    @Override
    public int applyAsInt(@NotNull final Picture picture) {
        return EnumUtils.ordinal(picture.getChain());
    }
}
