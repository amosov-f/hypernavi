package ru.hypernavi.core.classify.scheme.feature;

import org.openimaj.image.MBFImage;
import org.openimaj.image.processing.edges.CannyEdgeDetector;
import ru.hypernavi.core.classify.scheme.Picture;
import ru.hypernavi.ml.factor.Factor;

/**
 * Created by Константин on 09.09.2015.
 */
public class CannySummaryFactor extends Factor<Picture> {
    public CannySummaryFactor() {
        super("canny_summary");
    }

    @Override
    public double applyAsDouble(final Picture value) {
        final MBFImage image = value.getImage().clone();
        image.processInplace(new CannyEdgeDetector());
        final float[][] imageData = image.getPixelVectorNative(new float[image.getWidth() * image.getHeight()][3]);
        float sum = 0;
        for (final float[] pixels : imageData) {
            for (final float pixel : pixels)
                if (pixel > 0)
                    sum += 1;
        }
        return sum / imageData.length;
    }
}
