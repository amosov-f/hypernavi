package ru.hypernavi.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by amosov-f on 25.08.15.
 */
public enum MoreIOUtils {
    ;

    private static final Log LOG = LogFactory.getLog(MoreIOUtils.class);

    @Nullable
    public static InputStream getInputStream(@NotNull final String path) throws FileNotFoundException {
        return isClasspath(path) ? MoreIOUtils.class.getResourceAsStream(toResourceName(path)) : new FileInputStream(path);
    }

    @NotNull
    public static URL toURL(@NotNull final String path) throws MalformedURLException {
        return isClasspath(path) ? MoreIOUtils.class.getResource(toResourceName(path)) : new URL(path);
    }

    public static boolean isClasspath(@NotNull final String path) {
        return path.startsWith("classpath:");
    }

    @NotNull
    public static String toResourceName(@NotNull final String path) {
        // fix Windows-style path
        return path.replace('\\', '/').replace("classpath:", "");
    }

    @NotNull
    public static InputStream connect(@NotNull final String url) throws IOException {
        final URLConnection connection = new URL(url).openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        return connection.getInputStream();
    }

    @NotNull
    public static byte[] read(@NotNull final String url) throws IOException {
        try (final InputStream in = connect(url)) {
            return IOUtils.toByteArray(in);
        }
    }
}
