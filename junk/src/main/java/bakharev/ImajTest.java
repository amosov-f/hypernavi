package bakharev;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;


import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.ColourSpace;
import org.openimaj.image.processing.edges.CannyEdgeDetector;
import org.openimaj.ml.clustering.FloatCentroidsResult;
import org.openimaj.ml.clustering.assignment.HardAssigner;


/**
 * Created by Константин on 04.09.15.
 */
public enum ImajTest {
    ;

    private static void ShowClassters(final MBFImage image, final FloatCentroidsResult result, final float[][] centroids) {
        final HardAssigner<float[], ?, ?> assigner = result.defaultHardAssigner();
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                final float[] pixel = image.getPixelNative(x, y);
                final int centroid = assigner.assign(pixel);
                image.setPixelNative(x, y, centroids[centroid]);
            }
        }
        DisplayUtilities.display(ColourSpace.convert(image, ColourSpace.RGB));
    }

    public static void main(@NotNull final String[] args) throws IOException {
        final MBFImage image = ImageUtilities.readMBF(new URL("http://www.okmarket.ru/media/gallery/2012-11-16/market-tumen-big.jpg"));
        final MBFImage image2 = ImageUtilities.readMBF(new URL("http://www.okmarket.v/media/gallery/2012-06-08/okey_vokzalnaya.JPG"));
        // DisplayUtilities.display(image);
        image.processInplace(new CannyEdgeDetector());
        image2.processInplace(new CannyEdgeDetector());
        DisplayUtilities.display(image2);
        final float[][] imageData = image.getPixelVectorNative(new float[image.getWidth() * image.getHeight()][3]);
        float sum = 0;
        for (final float[] pixel : imageData) {
            sum += pixel[0] + pixel[1] + pixel[2];
        }

        System.out.println(sum / imageData.length);

        final float[][] imageData2 = image2.getPixelVectorNative(new float[image2.getWidth() * image2.getHeight()][3]);
        sum = 0;
        for (final float[] pixel : imageData2) {
            sum += pixel[0] + pixel[1] + pixel[2];
        }

        System.out.println(sum / imageData2.length);


// image = ColourSpace.convert(image, ColourSpace.CIE_Lab);
//        image2 = ColourSpace.convert(image2, ColourSpace.CIE_Lab);
//        FloatKMeans cluster = FloatKMeans.createExact(3);
//        float[][] imageData = image.getPixelVectorNative(new float[image.getWidth() * image.getHeight()][3]);
//        float[][] imageData2 = image2.getPixelVectorNative(new float[image2.getWidth() * image2.getHeight()][3]);
//
//        FloatCentroidsResult result = cluster.cluster(imageData);
//        float[][] centroids = result.centroids;
//        for (float[] fs : centroids) {
//            System.out.println(Arrays.toString(fs));
//        }
//        System.out.println();
//
//        FloatCentroidsResult result2 = cluster.cluster(imageData2);
//        float[][] centroids2 = result2.centroids;
//        for (float[] fs : centroids2) {
//            System.out.println(Arrays.toString(fs));
//        }
//
//        ShowClassters(image, result, centroids);
//        ShowClassters(image2, result2, centroids2);
    }
}
