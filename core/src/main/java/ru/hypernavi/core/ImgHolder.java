package ru.hypernavi.core;

import org.jetbrains.annotations.Nullable;

/**
 * Created by Константин on 15.08.2015.
 */
public interface ImgHolder {
    @Nullable
    byte[] getImage(final String md5Hash);
}
