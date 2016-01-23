package ru.hypernavi.core.classify.scheme;

import org.jetbrains.annotations.NotNull;

import java.util.*;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.xml.DOMConfigurator;
import org.openimaj.feature.DoubleFVComparison;
import ru.hypernavi.commons.Chain;
import ru.hypernavi.core.classify.scheme.answer.ChainAnswer;
import ru.hypernavi.core.classify.scheme.feature.*;
import ru.hypernavi.ml.classifier.BinaryClassifier;
import ru.hypernavi.ml.classifier.WekaClassifier;
import ru.hypernavi.ml.factor.CachedFactor;
import ru.hypernavi.ml.factor.Factor;
import ru.hypernavi.util.EnumUtils;
import ru.hypernavi.util.MoreIOUtils;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.RandomForest;

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
        new CachedFactor<>(new TextRectangleFeature()),
        new DiagonalFeature(),
        new CachedFactor<>(new CannySummaryFactor()),
        new AreaFeature()
    );

    public SchemeClassifier() {
        super(new RandomForest(), FEATURES, new ChainAnswer(), SchemeClassifier::toString);
    }

    @Override
    public boolean isClass(@NotNull final Picture picture) {
        return classify(picture) >= 1;
    }

    @NotNull
    private static String toString(final int chainOrdinal) {
        return Optional.ofNullable(EnumUtils.instance(Chain.class, chainOrdinal)).map(Enum::name).orElse("none").toLowerCase();
    }

    public static SchemeClassifier getClassifier() throws Exception {
        DOMConfigurator.configure(MoreIOUtils.toURL("classpath:/log4j.xml"));
        final SchemeClassifier classifier = new SchemeClassifier();
        classifier.learn(Picture.download());
        final Evaluation evaluation = classifier.crossValidate();

        LOG.info(evaluation.toMatrixString());
        LOG.info(evaluation.toClassDetailsString());
        LOG.info(evaluation.toSummaryString());

        return classifier;
    }

    public static void main(@NotNull final String[] args) throws Exception {
        final SchemeClassifier classifier = getClassifier();

        final Picture[] newPictures = Picture.downloadFromFile("urls.txt");
        final int[][] matrix = new int[7][7];
        int correct = 0;
        int fail = 0;
        for (final Picture picture : newPictures) {
            final int i = picture.getChain() == null ? 0 : picture.getChain().ordinal() + 1;
            final int j = classifier.classify(picture);
            if (i == j) {
                correct++;
            } else {
                fail++;
            }
            matrix[i][j]++;
        }
        final int total = correct + fail;

        for (int i = 0; i < 7; ++i) {
            String row = "";
            for (int j = 0; j < 7; ++j) {
                row = row + String.format("%3s ", matrix[i][j]);
            }
            LOG.info(row);
        }

        //noinspection MagicNumber
        LOG.info(String.format("%-35s\t\t%d\t\t%.4f\tprocents", "Correctly Classified Instances",  correct, 100.0 * correct / total));
        //noinspection MagicNumber
        LOG.info(String.format("%-35s\t\t%d\t\t%.4f\tprocents", "Incorrectly Classified Instances", fail, 100.0 * fail / total));


        final String tpUrl = "http://www.okmarket.ru/media/gallery/2012-07-27/plan_zala_big.jpg";
        final String tnUrl = "http://cs5134.vk.me/v5134803/12d4/h9q-y0sO6Ko.jpg";
        final Picture tpPicture = Objects.requireNonNull(Picture.download(tpUrl));
        final Picture tnPicture = Objects.requireNonNull(Picture.download(tnUrl));
        LOG.info(tpUrl + " -> " + toString(classifier.classify(tpPicture)) + ", " + classifier.isClass(tpPicture));
        LOG.info(tnUrl + " -> " + toString(classifier.classify(tnPicture)) + ", " + classifier.isClass(tnPicture));
    }

    public void manyClassify(final String fileName) {

    }
}

