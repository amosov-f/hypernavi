package amosov;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;


import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;

/**
 * Created by amosov-f on 29.08.15.
 */
public enum ImajTest {
    ;

    public static void main(@NotNull final String[] args) throws IOException {
        final MBFImage image = ImageUtilities.readMBF(new File("img/4A1D07D7CF511735F7683FB681EA52DA.jpg"));
        System.out.println(image.colourSpace);
        DisplayUtilities.display(image);
        DisplayUtilities.display(image.getBand(0), "Red Channel");
    }
}
