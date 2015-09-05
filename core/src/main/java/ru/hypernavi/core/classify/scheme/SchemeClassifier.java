package ru.hypernavi.core.classify.scheme;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;
import java.util.Random;


import org.apache.commons.io.IOUtils;
import ru.hypernavi.commons.Chain;
import ru.hypernavi.core.classify.scheme.answer.ChainAnswer;
import ru.hypernavi.core.classify.scheme.feature.AuchanHostnameFeature;
import ru.hypernavi.core.classify.scheme.feature.OkeyHostnameFeature;
import ru.hypernavi.ml.classifier.BinaryClassifier;
import ru.hypernavi.ml.classifier.WekaClassifier;
import ru.hypernavi.util.EnumUtils;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.core.Instances;

/**
 * Created by amosov-f on 03.09.15.
 */
public final class SchemeClassifier extends WekaClassifier<Picture> implements BinaryClassifier<Picture> {
    public SchemeClassifier(@NotNull final Picture... dataset) {
        super(new SMO(), Arrays.asList(new OkeyHostnameFeature(), new AuchanHostnameFeature()), new ChainAnswer(), SchemeClassifier::toString, dataset);
    }

    @Override
    public boolean isClass(@NotNull final Picture picture) {
        return classify(picture) >= 1;
    }

    @NotNull
    private static String toString(final int chainOrdinal) {
        return Optional.ofNullable(EnumUtils.instance(Chain.class, chainOrdinal)).map(Enum::name).orElse("none").toLowerCase();
    }

    public static void main(@NotNull final String[] args) throws Exception {
        final Picture[] pictures = IOUtils.readLines(SchemeClassifier.class.getResourceAsStream("/dataset/chains.txt")).stream()
                .map(line -> line.split("\t"))
                .map(parts -> Picture.download(parts[1], Chain.parse(parts[0])))
                .toArray(Picture[]::new);
        final SchemeClassifier classifier = new SchemeClassifier(pictures);
        final Instances instances = classifier.toInstances(pictures);
        final Evaluation evaluation = new Evaluation(instances);
        evaluation.crossValidateModel(classifier.getWekaClassifier(), instances, 5, new Random(0));
        final String tpUrl = "http://www.okmarket.ru/media/gallery/2012-07-27/plan_zala_big.jpg";
        final String tnUrl = "http://cs5134.vk.me/v5134803/12d4/h9q-y0sO6Ko.jpg";
        final Picture tpPicture = Picture.download(tpUrl);
        final Picture tnPicture = Picture.download(tnUrl);
        System.out.println(tpUrl + " -> " + toString(classifier.classify(tpPicture)) + ", " + classifier.isClass(tpPicture));
        System.out.println(tnUrl + " -> " + toString(classifier.classify(tnPicture)) + ", " + classifier.isClass(tnPicture));
        System.out.println();
        System.out.println(evaluation.toMatrixString());
        System.out.println(evaluation.toClassDetailsString());
        System.out.println(evaluation.toSummaryString());
    }
}
