package polyakov;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URL;
import java.util.Map;

import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.processing.resize.ResizeProcessor;
import org.openimaj.image.text.extraction.LiuSamarabanduTextExtractorBasic;
import org.openimaj.math.geometry.shape.Rectangle;
import org.openimaj.util.pair.IndependentPair;
import ru.hypernavi.util.MoreIOUtils;

/**
 * Created by Acer on 08.09.2015.
 */
public enum TextAreaCounter {
    ;

    public static void main(@NotNull final String[] args) throws IOException {
        try {
            final InputStream fromResourcesStream = MoreIOUtils.getInputStream("classpath:/urls.txt");
            final BufferedReader reader = new BufferedReader(new InputStreamReader(fromResourcesStream));
            String str;

            final PrintStream outputStream = new PrintStream(new File("data/urlsRectangles.txt"));
            while ((str = reader.readLine()) != null) {
                final FImage testImage = ImageUtilities.readF(new URL(str)).normalise().process(new ResizeProcessor(620));
                final FImage copyOfTestImage = testImage.clone();
                final LiuSamarabanduTextExtractorBasic te = new LiuSamarabanduTextExtractorBasic();
                te.processImage(testImage);
                te.processFeatureMap(testImage, copyOfTestImage);
                final Map<Rectangle, IndependentPair<FImage, String>> rectangles = te.getText();

                System.out.println(rectangles.size());
                outputStream.println(rectangles.size() + "   " + str);
                System.out.println(str);
            }
            outputStream.close();
            fromResourcesStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
