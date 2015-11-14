package ru.hypernavi.core.auth;

import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import ru.hypernavi.core.session.Property;
import ru.hypernavi.core.session.Session;
import ru.hypernavi.core.session.SessionInitializationException;

import static ru.hypernavi.core.session.SessionInitializationException.Error.FORBIDDEN;

/**
 * Created by amosov-f on 14.11.15.
 */
public class AdminRequestReader extends VkAuthRequestReader {
    @NotNull
    private final int[] adminUids;

    public AdminRequestReader(@NotNull final HttpServletRequest req, @NotNull final VkAuthValidator validator) {
        super(req, validator);
        try {
            adminUids = IOUtils.readLines(getClass().getResourceAsStream("/auth/vk/admins.txt"), StandardCharsets.UTF_8).stream()
                    .mapToInt(Integer::parseInt)
                    .toArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void validate(@NotNull final Session session) throws SessionInitializationException {
        super.validate(session);
        if (!ArrayUtils.contains(adminUids, session.demand(Property.VK_USER).getUid())) {
            throw new SessionInitializationException(FORBIDDEN, "Only admins have access to this page!");
        }
    }
}
