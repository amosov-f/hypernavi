package ru.hypernavi.core.classify.scheme;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URL;


import org.apache.commons.lang3.ArrayUtils;
import ru.hypernavi.commons.Chain;

/**
 * Created by amosov-f on 03.09.15.
 */
public final class Picture {
    @NotNull
    private final URL url;
    @NotNull
    private final byte[] bytes;
    @Nullable
    private Chain chain;

    public Picture(@NotNull final URL url, @NotNull final byte[] bytes) {
        this.url = url;
        this.bytes = bytes;
    }

    @NotNull
    public URL getUrl() {
        return url;
    }

    @NotNull
    public byte[] getBytes() {
        return bytes;
    }

    @Nullable
    public Chain getChain() {
        return chain;
    }

    public void setChain(@Nullable final Chain chain) {
        this.chain = chain;
    }

    @NotNull
    public static Picture download(@NotNull final String url, @Nullable final Chain chain) {
        final Picture picture = download(url);
        picture.setChain(chain);
        return picture;
    }

    @NotNull
    public static Picture download(@NotNull final String url) {
        try {
            // TODO: download picture by url
            return new Picture(new URL(url), ArrayUtils.EMPTY_BYTE_ARRAY);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
