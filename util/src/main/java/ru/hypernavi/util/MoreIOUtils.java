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
        if (path.startsWith("classpath")) {
            return MoreIOUtils.class.getResourceAsStream(path
                    .replace('\\', '/') // fix Windows-style path
                    .replace("classpath:", ""));
        }
        return new FileInputStream(path);
    }

    @NotNull
    public static URL toURL(@NotNull final String path) throws MalformedURLException {
        return path.startsWith("classpath:") ? MoreIOUtils.class.getResource(path.replace("classpath:", "")) : new URL(path);
    }
}
