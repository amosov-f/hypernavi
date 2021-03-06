package ru.hypernavi.ml;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.hypernavi.ml.factor.Factor;
import weka.classifiers.Classifier;
import weka.classifiers.evaluation.Evaluation;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by amosov-f on 23.01.16.
 */
public abstract class WekaAdapter<T> {
    @NotNull
    protected final Classifier classifier;
    @NotNull
    private final List<? extends Factor<T>> features;

    @Nullable
    private ArrayList<Attribute> attributes;
    @Nullable
    protected Attribute classAttribute;
    @Nullable
    protected Instances instances;

    protected WekaAdapter(@NotNull final Classifier classifier, @NotNull final List<? extends Factor<T>> features) {
        this.classifier = classifier;
        this.features = features;
    }

    public void init(@NotNull final T...dataset) {
        classAttribute = getClassAttribute(dataset);
        attributes = new ArrayList<>(features.stream()
            .map(feature -> new Attribute(feature.getName()))
            .collect(Collectors.toList()));
        attributes.add(classAttribute);
        instances = new Instances("dataset", attributes, dataset.length);
        for (final T object : dataset) {
            final Instance instance = toInstance(object, false);
            instance.setDataset(instances);
            instances.add(instance);
        }
        instances.setClass(classAttribute);
    }

    public void learn(@NotNull final T... dataset) {
        init(dataset);
        try {
            this.classifier.buildClassifier(instances);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public weka.classifiers.evaluation.Evaluation crossValidate() {
        Objects.requireNonNull(instances, "Learn classifier before!");
        try {
            final Evaluation evaluation = new Evaluation(instances);
            evaluation.crossValidateModel(classifier, instances, instances.size(), new Random(0));
            return evaluation;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    protected Instance toInstance(@NotNull final T object, final boolean classIsMissing) {
        Objects.requireNonNull(attributes, "Learn classifier before!");
        Objects.requireNonNull(instances, "Learn classifier before!");
        if (object instanceof HugeObject) {
            ((HugeObject) object).fill();
        }
        final Instance instance = new DenseInstance(attributes.size());
        for (int i = 0; i < features.size(); i++) {
            instance.setValue(attributes.get(i), features.get(i).applyAsDouble(object));
        }
        if (!classIsMissing) {
            setClassValue(instance, object);
        }
        instance.setDataset(instances);
        if (object instanceof HugeObject) {
            ((HugeObject) object).empty();
        }
        return instance;
    }

    @NotNull
    protected abstract Attribute getClassAttribute(@NotNull final T... dataset);

    protected abstract void setClassValue(@NotNull final Instance instance, @NotNull final T object);
}
