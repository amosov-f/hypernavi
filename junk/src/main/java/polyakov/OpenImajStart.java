package polyakov;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;


import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.pixel.statistics.HistogramModel;
import org.openimaj.image.processing.edges.CannyEdgeDetector;
import org.openimaj.math.statistics.distribution.MultidimensionalHistogram;
import org.openimaj.feature.DoubleFVComparison;

/**
 * Created by Acer on 05.09.2015.
 */
public enum OpenImajStart {
    ;

    public static void main (@NotNull final String[] args) throws IOException {
        final MBFImage okeyImage1 = ImageUtilities.readMBF(new File("data/img/4A1D07D7CF511735F7683FB681EA52DA.jpg"));
        //final MBFImage okeyImage2 = ImageUtilities.readMBF(new File("data/img/44B7EE44A7015E9F71C26F988EAA195B.jpg"));
        final MBFImage okeyImage2 = ImageUtilities.readMBF(new File("data/img/NotFound.jpg"));
        System.out.println("ImageHeight: " + okeyImage1.getHeight() + " ImageWidth: " + okeyImage1.getWidth());
        final Float[] myPixel = okeyImage1.getPixel(0, 0);
        System.out.println(Arrays.toString(myPixel));
        okeyImage1.processInplace(new CannyEdgeDetector());
        DisplayUtilities.display(okeyImage2);
        //
        final HistogramModel model = new HistogramModel(4, 4, 4);
        model.estimateModel(okeyImage1);
        final MultidimensionalHistogram histogram1 = model.histogram.clone();
        model.estimateModel(okeyImage2);
        final MultidimensionalHistogram histogram2 = model.histogram.clone();
        final double distanceScore = histogram1.compare( histogram2, DoubleFVComparison.EUCLIDEAN);
        System.out.println("histogramm distance " + distanceScore);
        System.out.println("histogramm distance " + histogram1.compare(histogram1, DoubleFVComparison.EUCLIDEAN));
    }
}
