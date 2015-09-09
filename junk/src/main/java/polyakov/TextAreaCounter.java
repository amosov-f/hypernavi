package polyakov;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
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
        final InputStream fromResourcesStream = MoreIOUtils.getInputStream("classpath:/urls.txt");
        final List<String> strings = IOUtils.readLines(fromResourcesStream);
        final List<String> answer = new ArrayList<>();
        for (final String str : strings) {
            final FImage testImage = ImageUtilities.readF(new URL(str)).normalise().process(new ResizeProcessor(620));
            final FImage copyOfTestImage = testImage.clone();
            final LiuSamarabanduTextExtractorBasic te = new LiuSamarabanduTextExtractorBasic();
            te.processImage(testImage);
            te.processFeatureMap(testImage, copyOfTestImage);
            final Map<Rectangle, IndependentPair<FImage, String>> rectangles = te.getText();
            answer.add(rectangles.size() + "\t" + str);
        }
        final File outputFile = new File("data/urlsRectangles.txt");
        FileUtils.writeLines(outputFile, answer);
        if (fromResourcesStream != null) {
            fromResourcesStream.close();
        }
    }
}
