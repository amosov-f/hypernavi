package ru.hypernavi.ml.classifier;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.IntFunction;
import java.util.stream.Collectors;


import ru.hypernavi.ml.WekaAdapter;
import ru.hypernavi.ml.factor.ClassFactor;
import ru.hypernavi.ml.factor.Factor;
import weka.core.Attribute;
import weka.core.Instance;

/**
 * Created by amosov-f on 03.09.15.
 */
public class WekaClassifier<T> extends WekaAdapter<T> implements Classifier<T> {
    @NotNull
    private final IntFunction<String> class2string;
    @NotNull
    private final ClassFactor<T> answer;

    public WekaClassifier(@NotNull final weka.classifiers.Classifier classifier,
                          @NotNull final List<? extends Factor<T>> features,
                          @NotNull final ClassFactor<T> answer,
                          @NotNull final IntFunction<String> class2string)
    {
        super(classifier, features);
        this.answer = answer;
        this.class2string = class2string;
    }

    @Override
    public int classify(@NotNull final T object) {
        try {
            return (int) classifier.classifyInstance(toInstance(object, true));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    @Override
    protected Attribute getClassAttribute(@NotNull final T... dataset) {
        final List<String> classNames = Arrays.stream(dataset)
                .mapToInt(answer::applyAsInt)
                .sorted()
                .distinct()
                .mapToObj(class2string::apply)
                .collect(Collectors.toList());
        return new Attribute(answer.getName(), classNames);
    }

    @Override
    protected void setClassValue(@NotNull final Instance instance, @NotNull final T object) {
        Objects.requireNonNull(classAttribute, "Learn classifier before!");
        instance.setValue(classAttribute, class2string.apply(answer.applyAsInt(object)));
    }
}
