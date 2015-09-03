package ru.hypernavi.core.classify.scheme;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;


import ru.hypernavi.ml.factor.BinaryFactor;

/**
 * Created by amosov-f on 03.09.15.
 */
public final class SchemeAnswer extends BinaryFactor<Picture> {
    public SchemeAnswer() {
        super("is_scheme");
    }

    @Override
    public boolean test(@NotNull final Picture picture) {
        return Optional.ofNullable(picture.isScheme()).orElseThrow(() -> new IllegalStateException("No scheme info about this picture!"));
    }
}
