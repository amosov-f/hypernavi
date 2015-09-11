package ru.hypernavi.ml.factor;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Константин on 11.09.2015.
 */
public interface CacheableObject {
    @NotNull
    String hash();
}
