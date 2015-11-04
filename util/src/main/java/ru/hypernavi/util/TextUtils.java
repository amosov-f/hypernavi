package ru.hypernavi.util;

import org.jetbrains.annotations.NotNull;

/**
 * Created by amosov-f on 04.11.15.
 */
public enum TextUtils {
    ;

    @NotNull
    public static String limit(@NotNull final String s, final int limit) {
        return limit < s.length() ? s.substring(0, limit) : s;
    }
}
