package ru.hypernavi.core.auth;

import org.jetbrains.annotations.NotNull;


import net.jcip.annotations.Immutable;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * Created by amosov-f on 14.11.15.
 */
@Immutable
public final class VkAuthValidator {
    private final int appId;
    @NotNull
    private final String secretKey;

    public VkAuthValidator(final int appId, @NotNull final String secretKey) {
        this.appId = appId;
        this.secretKey = secretKey;
    }

    public boolean validate(@NotNull final VkUser user, @NotNull final String hash) {
        return DigestUtils.md5Hex(appId + (user.getUid() + secretKey)).equals(hash);
    }
}
