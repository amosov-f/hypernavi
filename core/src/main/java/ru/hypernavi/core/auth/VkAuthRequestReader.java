package ru.hypernavi.core.auth;

import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import ru.hypernavi.core.session.Property;
import ru.hypernavi.core.session.RequestReader;
import ru.hypernavi.core.session.Session;
import ru.hypernavi.core.session.param.CookieParam;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by amosov-f on 14.11.15.
 */
public class VkAuthRequestReader extends RequestReader {
    protected static final CookieParam<VkUser> COOKIE_VK_USER = new CookieParam.ObjectParam<>("vk_user", VkUser.class);

    @NotNull
    private final int[] adminUids;

    public VkAuthRequestReader(@NotNull final HttpServletRequest req) {
        super(req);
        final InputStream in = getClass().getResourceAsStream("/auth/vk/admins.txt");
        adminUids = new BufferedReader(new InputStreamReader(in)).lines().mapToInt(Integer::parseInt).toArray();
    }

    @Override
    public void initialize(@NotNull final Session session) {
        super.initialize(session);
        setPropertyIfPresent(session, Property.VK_USER, COOKIE_VK_USER);
        session.set(Property.IS_ADMIN, isAdmin(session));
    }

    private boolean isAdmin(@NotNull final Session session) {
        if (session.demand(Property.PLATFORM).isLocal()) {
            return true;
        }
        return session.getOptional(Property.VK_USER)
                .map(user -> ArrayUtils.contains(adminUids, user.getUid()))
                .orElse(false);
    }
}
