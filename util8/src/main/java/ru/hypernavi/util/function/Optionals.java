package ru.hypernavi.util.function;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Created by amosov-f on 14.11.15.
 */
public enum Optionals {
    ;

    public static <T> boolean ifPresent(@Nullable final T value, @NotNull final Consumer<? super T> consumer) {
        if (value == null) {
            return false;
        }
        consumer.accept(value);
        return true;
    }
}
