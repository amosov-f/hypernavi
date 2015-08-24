package ru.hypernavi.core;

import org.jetbrains.annotations.Nullable;

import java.io.*;


import org.apache.commons.io.IOUtils;
import sun.nio.ch.IOUtil;


/**
 * Created by Константин on 24.08.2015.
 */
public class FileImgLoader implements ImgHolder {
    @Nullable
    @Override
    public byte[] getImage(final String md5Hash) {
        byte[] result = null;
        try {
            final InputStream in = new FileInputStream(new File("./img" + md5Hash));
            result = IOUtils.toByteArray(in);
        } catch (IOException ignored) {
        }
        return result;
    }
}
