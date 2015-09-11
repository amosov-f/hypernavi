package ru.hypernavi.core.classify.scheme.feature;

import java.util.Map;
import java.util.logging.Logger;


import org.openimaj.image.FImage;
import org.openimaj.image.processing.resize.ResizeProcessor;
import org.openimaj.image.text.extraction.LiuSamarabanduTextExtractorBasic;
import org.openimaj.math.geometry.shape.Rectangle;
import org.openimaj.util.pair.IndependentPair;
import ru.hypernavi.core.classify.scheme.Picture;
import ru.hypernavi.ml.factor.Factor;

/**
 * Created by Acer on 08.09.2015.
 */
public class TextRectangleFeature extends Factor<Picture> {
    private static final Logger LOG = Logger.getLogger(TextRectangleFeature.class.getName());

    public TextRectangleFeature() {
        super("text_rectangle");
    }


    @Override
    public double applyAsDouble(final Picture value) {
        final FImage testImage = value.getImage().flatten().normalise().process(new ResizeProcessor(620));
        final FImage copyOfTestImage = testImage.clone();
        final LiuSamarabanduTextExtractorBasic te = new LiuSamarabanduTextExtractorBasic();
        te.processImage(testImage);
        te.processFeatureMap(testImage, copyOfTestImage);
        final Map<Rectangle, IndependentPair<FImage, String>> rectangles = te.getText();
        return rectangles.size();
    }
}