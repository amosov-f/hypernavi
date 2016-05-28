package ru.hypernavi.ml.regression.map;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

/**
 * Created by amosov-f on 28.05.16.
 */
public final class ValidationResult {
    @NotNull
    private final Point[] diffs;
    @NotNull
    private final String evalX;
    @NotNull
    private final String evalY;

    public ValidationResult(@NotNull final Point[] diffs, @NotNull final String evalX, @NotNull final String evalY) {
        this.diffs = diffs;
        this.evalX = evalX;
        this.evalY = evalY;
    }
}
