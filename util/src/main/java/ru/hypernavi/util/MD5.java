package ru.hypernavi.util;

import org.jetbrains.annotations.NotNull;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Константин on 14.08.2015.
 */
public enum MD5 {
    ;

    @NotNull
    public static String generate(@NotNull final byte[] input) {
        try {
            final MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(input);
            // TODO amosov-f: double digest ?!?
            return new HexBinaryAdapter().marshal(messageDigest.digest(messageDigest.digest()));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
