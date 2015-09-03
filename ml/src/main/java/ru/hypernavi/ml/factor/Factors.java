package ru.hypernavi.ml.factor;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by amosov-f on 03.09.15.
 */
public final class Factors<T> {
    @NotNull
    private final List<Factor<T>> features = new ArrayList<>();
    @NotNull
    private final Factor<T> answer;

    public Factors(@NotNull final List<? extends Factor<T>> features, @NotNull final Factor<T> answer) {
        this.features.addAll(features);
        this.answer = answer;
    }

    @NotNull
    public List<Factor<T>> getFeatures() {
        return features;
    }

    @NotNull
    public Factor<T> getAnswer() {
        return answer;
    }

    @NotNull
    public List<Factor<T>> getFactors() {
        return Stream.concat(features.stream(), Stream.of(answer)).collect(Collectors.toList());
    }
}
