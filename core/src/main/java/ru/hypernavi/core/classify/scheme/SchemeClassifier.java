package ru.hypernavi.core.classify.scheme;

import org.jetbrains.annotations.NotNull;

import java.util.*;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.xml.DOMConfigurator;
import org.openimaj.feature.DoubleFVComparison;
import ru.hypernavi.commons.Chain;
import ru.hypernavi.core.classify.scheme.answer.SchemeAnswer;
import ru.hypernavi.core.classify.scheme.feature.*;
import ru.hypernavi.ml.classifier.BinaryClassifier;
import ru.hypernavi.ml.classifier.WekaClassifier;
import ru.hypernavi.ml.factor.Factor;
import ru.hypernavi.util.EnumUtils;
import ru.hypernavi.util.MoreIOUtils;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

/**
 * Created by amosov-f on 03.09.15.
 */
public final class SchemeClassifier extends WekaClassifier<Picture> implements BinaryClassifier<Picture> {
    private static final Log LOG = LogFactory.getLog(SchemeClassifier.class);

    private static final List<? extends Factor<Picture>> FEATURES = Arrays.asList(
           // new OkeyHostnameFeature(),
            new HistogramFeature(DoubleFVComparison.CHI_SQUARE),
            new HistogramFeature(DoubleFVComparison.HAMMING),
          //  new AuchanHostnameFeature(),
            new TextRectangleFeature(),
            new DiagonalFeature(),
            new CannySummaryFactor(),
            new AreaFeature()
    );

    public SchemeClassifier(@NotNull final Picture... dataset) {
        super(new RandomForest(), FEATURES, new SchemeAnswer(), SchemeClassifier::toString, dataset);
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
        DOMConfigurator.configure(MoreIOUtils.toURL("classpath:/log4j.xml"));
        final SchemeClassifier classifier = new SchemeClassifier(Picture.download());
        final Instances instances = classifier.getInstances();
        final Evaluation evaluation = new Evaluation(instances);
        evaluation.crossValidateModel(classifier.getWekaClassifier(), instances, instances.size(), new Random(0));
        final String tpUrl = "http://www.okmarket.ru/media/gallery/2012-07-27/plan_zala_big.jpg";
        final String tnUrl = "http://cs5134.vk.me/v5134803/12d4/h9q-y0sO6Ko.jpg";
        final Picture tpPicture = Objects.requireNonNull(Picture.download(tpUrl));
        final Picture tnPicture = Objects.requireNonNull(Picture.download(tnUrl));
        LOG.info(tpUrl + " -> " + toString(classifier.classify(tpPicture)) + ", " + classifier.isClass(tpPicture));
        LOG.info(tnUrl + " -> " + toString(classifier.classify(tnPicture)) + ", " + classifier.isClass(tnPicture));
        LOG.info(evaluation.toMatrixString());
        LOG.info(evaluation.toClassDetailsString());
        LOG.info(evaluation.toSummaryString());
    }
}
