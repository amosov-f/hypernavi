package bakharev;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;


import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.processing.resize.ResizeProcessor;
import org.openimaj.image.text.extraction.LiuSamarabanduTextExtractorBasic;
import org.openimaj.math.geometry.shape.Rectangle;
import org.openimaj.util.pair.IndependentPair;


/**
 * Created by Константин on 04.09.15.
 */
public enum ImajTest {
    ;
    public static void main(@NotNull final String[] args) throws IOException {
        final URL u = new URL("https://pp.vk.me/c625225/v625225330/4206f/d-fq2V1rfc8.jpg");
        final FImage testImage = ImageUtilities.readF(u).normalise().process(new ResizeProcessor(620));
        final FImage original = testImage.clone();
        final FImage ttestImage = testImage.clone();

        // Process the image
        DisplayUtilities.display(original);
        final LiuSamarabanduTextExtractorBasic te = new LiuSamarabanduTextExtractorBasic();
        te.processImage(testImage);
        te.processFeatureMap(testImage, ttestImage);

        Map<Rectangle, IndependentPair<FImage, String>> YO = te.getText();
        List<String> words = te.getTextStrings();
        words.forEach(System.out::println);
        for (final IndependentPair<FImage, String> p: YO.values()) {
           // DisplayUtilities.display(p.firstObject());
            System.out.println(p.getSecondObject());
        }

        final Map<Rectangle, FImage> imageRegions = te.getTextRegions();
        //System.out.println(imageRegions);

        final MBFImage img = MBFImage.createRGB(original);
        for (final Rectangle r : imageRegions.keySet())
            img.drawShape(r, 3, RGBColour.RED);

        DisplayUtilities.display(img);
    }
}
