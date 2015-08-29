package ru.hypernavi.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

/**
 * Created by amosov-f on 29.08.15.
 */
public enum  MoreReflectionUtils {
    ;

    @NotNull
    public static <T> T invoke(@NotNull final Class<?> clazz, @NotNull final String name, @NotNull final Object... args) {
        return Objects.requireNonNull(MoreReflectionUtils.<T>invokeNullable(clazz, name, args));
    }

    @Nullable
    public static <T> T invokeNullable(@NotNull final Class<?> clazz, @NotNull final String name, @NotNull final Object... args) {
        final Class<?>[] argClasses = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            argClasses[i] = args[i].getClass();
        }
        try {
            //noinspection unchecked
            return (T) clazz.getMethod(name, argClasses).invoke(null, args);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
