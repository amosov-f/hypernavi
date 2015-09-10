package ru.hypernavi.core.classify.scheme.feature;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.processing.resize.ResizeProcessor;
import org.openimaj.image.text.extraction.LiuSamarabanduTextExtractorBasic;
import org.openimaj.math.geometry.shape.Rectangle;
import org.openimaj.util.pair.IndependentPair;
import ru.hypernavi.core.classify.scheme.Picture;
import ru.hypernavi.ml.factor.CachedFactor;

/**
 * Created by Acer on 08.09.2015.
 */
public class TextRectangleFeature extends CachedFactor<Picture> {
    private static final Logger LOG = Logger.getLogger(TextRectangleFeature.class.getName());

    public TextRectangleFeature() {
        super("text_rectangle", true);
    }

    @Override
    public double applyCachedDouble(@NotNull final Picture value) {
        final FImage testImage;
        try {
            testImage = ImageUtilities.readF(value.getUrl()).normalise().process(new ResizeProcessor(620));
        } catch (IOException e) {
            LOG.log(Level.WARNING, "can't read image from URL\n" + e.getMessage());
            return 0;
        }
        final FImage copyOfTestImage = testImage.clone();
        final LiuSamarabanduTextExtractorBasic te = new LiuSamarabanduTextExtractorBasic();
        te.processImage(testImage);
        te.processFeatureMap(testImage, copyOfTestImage);
        final Map<Rectangle, IndependentPair<FImage, String>> rectangles = te.getText();
        return rectangles.size();
    }
}