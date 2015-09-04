package ru.hypernavi.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by amosov-f on 05.09.15.
 */
public enum EnumUtils {
    ;

    public static int ordinal(@Nullable final Enum<?> instance) {
        return instance == null ? 0 : instance.ordinal() + 1;
    }

    @Nullable
    public static <T extends Enum<T>> T instance(@NotNull final Class<T> enumClass, final int ordinal) {
        return ordinal == 0 ? null : enumClass.getEnumConstants()[ordinal - 1];
    }
}
