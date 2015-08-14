package ru.hypernavi.core;

import javax.imageio.ImageIO;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


import org.apache.commons.io.output.ByteArrayOutputStream;

/**
 * Created by Константин on 14.08.2015.
 */
public final class ImageHash {
    public static String generate(final BufferedImage image) {
        String hex = "default";
        try {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", baos);
            baos.flush();
            md.update(baos.toByteArray());
            baos.close();
            final byte[] byteData = md.digest();
            hex = (new HexBinaryAdapter()).marshal(md.digest(byteData));
        } catch (NoSuchAlgorithmException e) {

        } catch (IOException e) {

        }
        return hex;
    }
}
