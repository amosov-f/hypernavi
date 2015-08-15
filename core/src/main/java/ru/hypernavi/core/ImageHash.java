package ru.hypernavi.core;

import org.jetbrains.annotations.NotNull;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by Константин on 14.08.2015.
 */
public enum ImageHash {
    ;
    private static final Log LOG = LogFactory.getLog(ImageHash.class);

    @NotNull
    public static String generate(final byte[] image) {
        final String hex;
        try {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(image);
            final byte[] byteData = md.digest();
            hex = (new HexBinaryAdapter()).marshal(md.digest(byteData));
        } catch (NoSuchAlgorithmException e) {
            LOG.error("No MD5 hashing algorithm.");
            throw new RuntimeException(e.getMessage());
        }
        return hex;
    }
}
