package ru.hypernavi.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by amosov-f on 25.08.15.
 */
public enum MoreIOUtils {
    ;

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
}
