package ru.hypernavi.ml.classifier;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.Collectors;


import ru.hypernavi.ml.factor.ClassFactor;
import ru.hypernavi.ml.factor.Factor;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Created by amosov-f on 03.09.15.
 */
public class WekaClassifier<T> implements Classifier<T> {
    @NotNull
    private final weka.classifiers.Classifier classifier;
    @NotNull
    private final List<? extends Factor<T>> features;
    @NotNull
    private final ClassFactor<T> answer;
    @NotNull
    private final IntFunction<String> class2string;
    @NotNull
    private final ArrayList<Attribute> attributes;
    @NotNull
    private final Attribute classAttribute;
    @NotNull
    private final Instances instances;

    public WekaClassifier(@NotNull final weka.classifiers.Classifier classifier,
                          @NotNull final List<? extends Factor<T>> features,
                          @NotNull final ClassFactor<T> answer,
                          @NotNull final IntFunction<String> class2string,
                          @NotNull final T... dataset)
    {
        this.classifier = classifier;
        this.features = features;
        this.answer = answer;
        this.class2string = class2string;
        attributes = new ArrayList<>(features.stream()
                .map(feature -> new Attribute(feature.getName()))
                .collect(Collectors.toList()));
        classAttribute = new Attribute(
                answer.getName(),
                Arrays.stream(dataset)
                        .mapToInt(answer::applyAsInt)
                        .sorted()
                        .distinct()
                        .mapToObj(class2string::apply)
                        .collect(Collectors.toList())
        );
        attributes.add(classAttribute);
        instances = toInstances(dataset);
        try {
            classifier.buildClassifier(instances);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
    public Instances toInstances(@NotNull final T... dataset) {
        //noinspection LocalVariableHidesMemberVariable
        final Instances instances = new Instances("dataset", attributes, dataset.length);
        Arrays.stream(dataset).forEach(object -> instances.add(toInstance(object, false)));
        instances.setClass(classAttribute);
        return instances;
    }

    @NotNull
    private Instance toInstance(@NotNull final T object, final boolean classIsMissing) {
        final Instance instance = new DenseInstance(attributes.size());
        for (int i = 0; i < features.size(); i++) {
            instance.setValue(attributes.get(i), features.get(i).applyAsDouble(object));
        }
        if (!classIsMissing) {
            instance.setValue(classAttribute, class2string.apply(answer.applyAsInt(object)));
        }
        instance.setDataset(instances);
        return instance;
    }

    @NotNull
    public weka.classifiers.Classifier getWekaClassifier() {
        return classifier;
    }
}
