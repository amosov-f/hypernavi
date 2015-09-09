package ru.hypernavi.core.classify.scheme.feature;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


import org.apache.commons.io.IOUtils;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.processing.resize.ResizeProcessor;
import org.openimaj.image.text.extraction.LiuSamarabanduTextExtractorBasic;
import org.openimaj.math.geometry.shape.Rectangle;
import org.openimaj.util.pair.IndependentPair;
import ru.hypernavi.core.classify.scheme.Picture;
import ru.hypernavi.ml.factor.Factor;

/**
 * Created by Acer on 08.09.2015.
 */
public class OkeyTextFeature extends Factor<Picture> {
    private static final Logger LOG = Logger.getLogger(OkeyTextFeature.class.getName());
    private Map<URL, Double> textAreasNumbers;

    public OkeyTextFeature() {
        super("okey_text");
        textAreasNumbers = new HashMap<>();
        try {
            final InputStream rectanglesStream = new FileInputStream(new File("data/urlsRectangles.txt"));
            final List<String> strings = IOUtils.readLines(rectanglesStream);
            for (final String str : strings) {
                final String[] paths = str.split("\t");
                textAreasNumbers.put(new URL((paths[1])), Double.parseDouble(paths[0]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public double applyAsDouble(final Picture value) {
        final Double answer = textAreasNumbers.get(value.getUrl());
        if (answer != null) {
            return textAreasNumbers.get(value.getUrl());
        }
        try {
            final FImage testImage = ImageUtilities.readF(value.getUrl()).normalise().process(new ResizeProcessor(620));
            final FImage copyOfTestImage = testImage.clone();
            final LiuSamarabanduTextExtractorBasic te = new LiuSamarabanduTextExtractorBasic();
            te.processImage(testImage);
            te.processFeatureMap(testImage, copyOfTestImage);
            final Map<Rectangle, IndependentPair<FImage, String>> rectangles = te.getText();

            final File outputFile = new File("data/urlsRectangles.txt");
            final FileWriter fileWriter = new FileWriter(outputFile,true);
            final BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("\n" + rectangles.size() + "\t" + value.getUrl());
            bufferedWriter.close();

            return rectangles.size();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}