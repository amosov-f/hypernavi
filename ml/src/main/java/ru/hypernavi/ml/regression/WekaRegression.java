package ru.hypernavi.ml.regression;

import org.jetbrains.annotations.NotNull;
import ru.hypernavi.ml.WekaAdapter;
import ru.hypernavi.ml.factor.Factor;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.SerializationHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

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

    @NotNull
    public String serialize() {
        final ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try {
            SerializationHelper.write(bout, classifier);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Base64.getEncoder().encodeToString(bout.toByteArray());
    }

    @NotNull
    public static <T> WekaRegression<T> deserialize(@NotNull final String base64,
                                                    @NotNull final List<? extends Factor<T>> features,
                                                    @NotNull final Factor<T> answer)
    {
        final ByteArrayInputStream bin = new ByteArrayInputStream(Base64.getDecoder().decode(base64));
        try {
            final Classifier classifier = (Classifier) SerializationHelper.read(bin);
            return new WekaRegression<>(classifier, features, answer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
