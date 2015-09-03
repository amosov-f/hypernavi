package ru.hypernavi.core.classify.scheme;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Random;


import org.apache.commons.io.IOUtils;
import ru.hypernavi.core.classify.scheme.okey.HostnameFeature;
import ru.hypernavi.ml.classifier.BinaryClassifier;
import ru.hypernavi.ml.classifier.WekaClassifier;
import ru.hypernavi.ml.factor.Factors;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.core.Instances;

/**
 * Created by amosov-f on 03.09.15.
 */
public final class SchemeClassifier extends WekaClassifier<Picture> implements BinaryClassifier<Picture> {
    public SchemeClassifier(@NotNull final Picture... dataset) {
        super(new SMO(), new Factors<>(Collections.singletonList(new HostnameFeature()), new SchemeAnswer()), dataset);
    }

    @Override
    public boolean isClass(@NotNull final Picture picture) {
        return classify(picture) == 1;
    }

    public static void main(@NotNull final String[] args) throws Exception {
        final Picture[] pictures = IOUtils.readLines(SchemeClassifier.class.getResourceAsStream("/dataset/okey.txt")).stream()
                .map(line -> line.split("\t"))
                .map(parts -> Picture.download(parts[0], "1".equals(parts[1])))
                .toArray(Picture[]::new);
        final SchemeClassifier classifier = new SchemeClassifier(pictures);
        final Instances instances = classifier.toInstances(pictures);
        final Evaluation evaluation = new Evaluation(instances);
        evaluation.crossValidateModel(classifier.getWekaClassifier(), instances, 2, new Random(0));
        System.out.println("true positive prediction = " + classifier.isClass(Picture.download("http://www.okmarket.ru/media/gallery/2012-07-27/plan_zala_big.jpg")));
        System.out.println("true negative prediction = " + classifier.isClass(Picture.download("http://cs5134.vk.me/v5134803/12d4/h9q-y0sO6Ko.jpg")));
        System.out.println();
        System.out.println(evaluation.toMatrixString());
        System.out.println(evaluation.toClassDetailsString());
        System.out.println(evaluation.toSummaryString());
    }
}
