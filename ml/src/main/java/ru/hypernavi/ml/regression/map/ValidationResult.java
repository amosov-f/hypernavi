package ru.hypernavi.ml.regression.map;

import org.jetbrains.annotations.NotNull;
import weka.classifiers.evaluation.Evaluation;

import java.awt.*;

/**
 * Created by amosov-f on 28.05.16.
 */
public final class ValidationResult {
    @NotNull
    private final Point[] diffs;
    @NotNull
    private final transient Evaluation evalX;
    @NotNull
    private final transient Evaluation evalY;
    @NotNull
    private final String summaryEvalX;
    @NotNull
    private final String summaryEvalY;

    public ValidationResult(@NotNull final Point[] diffs, @NotNull final Evaluation evalX, @NotNull final Evaluation evalY) {
        this.diffs = diffs;
        this.evalX = evalX;
        this.evalY = evalY;
        this.summaryEvalX = evalX.toSummaryString().trim();
        this.summaryEvalY = evalY.toSummaryString().trim();
    }

    @NotNull
    public Evaluation getEvalX() {
        return evalX;
    }

    @NotNull
    public Evaluation getEvalY() {
        return evalY;
    }
}
