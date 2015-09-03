package ru.hypernavi.ml.classifier;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


import ru.hypernavi.ml.factor.Factor;
import ru.hypernavi.ml.factor.Factors;
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
    private final List<Factor<T>> factors;
    @NotNull
    private final ArrayList<Attribute> attributes;
    @NotNull
    private final Attribute classAttribute;
    @NotNull
    private final Instances instances;

    public WekaClassifier(@NotNull final weka.classifiers.Classifier classifier,
                          @NotNull final Factors<T> factors,
                          @NotNull final T... dataset)
    {
        this.classifier = classifier;
        this.factors = factors.getFactors();
        attributes = new ArrayList<>(factors.getFeatures().stream()
                .map(feature -> new Attribute(feature.getName()))
                .collect(Collectors.toList()));
        final Factor<? super T> answer = factors.getAnswer();
        classAttribute = new Attribute(
                answer.getName(),
                Arrays.stream(dataset)
                        .mapToDouble(answer::applyAsDouble)
                        .mapToObj(WekaClassifier::toIntString)
                        .sorted()
                        .distinct()
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
        Arrays.stream(dataset).forEach(x -> instances.add(toInstance(x, false)));
        instances.setClass(classAttribute);
        return instances;
    }

    @NotNull
    public Instance toInstance(@NotNull final T object, final boolean classIsMissing) {
        final Instance instance = new DenseInstance(attributes.size());
        for (int i = 0; i < factors.size(); i++) {
            final Attribute attribute = attributes.get(i);
            if (attribute.isNominal() && classIsMissing) {
                continue;
            }
            final double value = factors.get(i).applyAsDouble(object);
            if (attribute.isNominal()) {
                instance.setValue(attribute, toIntString(value));
            } else {
                instance.setValue(attribute, value);
            }
        }
        instance.setDataset(instances);
        return instance;
    }

    @NotNull
    public weka.classifiers.Classifier getWekaClassifier() {
        return classifier;
    }

    @NotNull
    private static <T> String toIntString(final double d) {
        return String.valueOf(Math.round(d));
    }
}
