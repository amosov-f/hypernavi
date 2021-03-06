package ru.hypernavi.junk.classify.scheme.feature;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


import org.openimaj.feature.DoubleFVComparison;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.pixel.statistics.HistogramModel;
import org.openimaj.math.statistics.distribution.MultidimensionalHistogram;
import ru.hypernavi.junk.classify.scheme.Picture;
import ru.hypernavi.ml.factor.Factor;

/**
 * Created by Acer on 05.09.2015.
 */
public class HistogramFeature extends Factor<Picture>{
    private static final Logger LOG = Logger.getLogger(HistogramFeature.class.getName());
    private final MBFImage okeySupportImage;
    private final MultidimensionalHistogram okeySupportHistogram;
    private final DoubleFVComparison metric;

    public HistogramFeature(final DoubleFVComparison metric) {
        super("okey_histogram_with" + metric.toString());
        this.metric = metric;
        okeySupportImage = extractSupportOkey();
        if (okeySupportImage != null) {
            final HistogramModel model = new HistogramModel(4, 4, 4);
            model.estimateModel(okeySupportImage);
            okeySupportHistogram = model.histogram;
        } else {
            okeySupportHistogram = null;
        }
    }

    @Nullable
    private MBFImage extractSupportOkey() {
        MBFImage okeyImage = null;
        try {
            okeyImage = ImageUtilities.readMBF(new File("data/img/4A1D07D7CF511735F7683FB681EA52DA.jpg"));
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Problem with extract file from data." + e);
        }
        return okeyImage;
    }

    @Override
    public double applyAsDouble(final Picture value) {
        if (okeySupportImage == null) {
            return 1000.0;
        }

        final HistogramModel model = new HistogramModel(4, 4, 4);
        model.estimateModel(value.getImage());
        final MultidimensionalHistogram valueImageHistogram = model.histogram;

        return okeySupportHistogram.compare(valueImageHistogram, metric);//DoubleFVComparison.CHI_SQUARE);
    }
}
