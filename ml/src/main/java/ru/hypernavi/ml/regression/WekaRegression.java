package ru.hypernavi.ml.regression;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;


import ru.hypernavi.ml.WekaAdapter;
import ru.hypernavi.ml.factor.Factor;
import weka.core.Attribute;
import weka.core.Instance;

/**
 * Created by amosov-f on 23.01.16.
 */
public class WekaRegression<T> extends WekaAdapter<T> implements Regression<T> {
    @NotNull
    private final Factor<T> answer;

    public WekaRegression(@NotNull final weka.classifiers.Classifier classifier,
                          @NotNull final List<? extends Factor<T>> features,
                          @NotNull final Factor<T> answer)
    {
        super(classifier, features);
        this.answer = answer;
    }

    @Override
    public double predict(@NotNull final T object) {
        try {
            return classifier.classifyInstance(toInstance(object, true));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    @Override
    protected Attribute getClassAttribute(@NotNull final T... dataset) {
        return new Attribute(answer.getName());
    }

    @Override
    protected void setClassValue(@NotNull final Instance instance, @NotNull final T object) {
        Objects.requireNonNull(classAttribute, "Learn classifier before!");
        instance.setValue(classAttribute, answer.applyAsDouble(object));
    }
}
