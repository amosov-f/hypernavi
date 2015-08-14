package ru.hypernavi.core;

import javax.imageio.ImageIO;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by Константин on 14.08.2015.
 */
public final class ImageHash {
    private static final Log LOG = LogFactory.getLog(ImageHash.class);
    public static String generate(final BufferedImage image) {
        final String hex;
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
            LOG.error("No MD5 hashing algorithm.");
            throw new RuntimeException(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return hex;
    }
}
