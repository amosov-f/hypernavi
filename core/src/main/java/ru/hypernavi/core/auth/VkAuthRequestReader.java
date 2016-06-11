package ru.hypernavi.core.auth;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.hypernavi.core.http.URIBuilder;
import ru.hypernavi.core.session.Property;
import ru.hypernavi.core.session.RequestReader;
import ru.hypernavi.core.session.Session;
import ru.hypernavi.core.session.SessionValidationException;
import ru.hypernavi.core.session.param.CookieParam;
import ru.hypernavi.core.session.param.NamedParam;
import ru.hypernavi.core.session.param.QueryParam;
import ru.hypernavi.util.function.Optionals;
import ru.hypernavi.util.json.GsonUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/**
 * Created by amosov-f on 14.11.15.
 */
public class VkAuthRequestReader extends RequestReader {
    private static final NamedParam<Integer> PARAM_UID = new QueryParam.IntegerParam("uid");
    private static final NamedParam<String> PARAM_FIRST_NAME = new QueryParam.StringParam("first_name");
    private static final NamedParam<String> PARAM_LAST_NAME = new QueryParam.StringParam("last_name");
    private static final NamedParam<String> PARAM_PHOTO = new QueryParam.StringParam("photo");
    private static final NamedParam<String> PARAM_PHOTO_REC = new QueryParam.StringParam("photo_rec");
    private static final NamedParam<String> PARAM_HASH = new QueryParam.StringParam("hash");

    private static final NamedParam<?>[] VK_PARAMS = new NamedParam[]{
            PARAM_UID, PARAM_FIRST_NAME, PARAM_LAST_NAME, PARAM_PHOTO, PARAM_PHOTO_REC, PARAM_HASH
    };

    private static final CookieParam<VkUser> COOKIE_VK_USER = new CookieParam.ObjectParam<>("vk_user", VkUser.class);
    private static final CookieParam<String> COOKIE_VK_HASH = new CookieParam.StringParam("vk_hash");

    public static final Property<VkAuthValidator> VALIDATOR = new Property<>("validator");

    public VkAuthRequestReader(@NotNull final HttpServletRequest req) {
        super(req);
    }

    @Override
    public void initialize(@NotNull final Session session) {
        super.initialize(session);

        setPropertyIfPresent(session, Property.VK_USER, COOKIE_VK_USER);
    }

    @Override
    public void validate(@NotNull final Session session) throws SessionValidationException {
        super.validate(session);
        if (getParamUser() != null && PARAM_HASH.contained(req)) {
            final URIBuilder builder = new URIBuilder(session.demand(Property.HTTP_REQUEST_URI));
            for (final NamedParam<?> param : VK_PARAMS) {
                builder.remove(param.getName());
            }
            final Cookie[] cookies = {
                    new Cookie(COOKIE_VK_USER.getName(), GsonUtils.gson().toJson(getParamUser())),
                    new Cookie(COOKIE_VK_HASH.getName(), PARAM_HASH.getValue(req))
            };
            for (final Cookie cookie : cookies) {
                cookie.setDomain("hypernavi.net");
                cookie.setMaxAge((int) Instant.now().plus(2, ChronoUnit.YEARS).getEpochSecond());
            }
            throw new SessionValidationException.Redirect("Vk params in request", builder.build().toString(), cookies);
        }
        final VkUser user = Optional.ofNullable(session.get(Property.VK_USER))
                .orElseThrow(() -> new SessionValidationException.UnAuthorized("Not enough vk auth parameters!"));
        final String hash = Optional.ofNullable(COOKIE_VK_HASH.getValue(req))
                .orElseThrow(() -> new SessionValidationException.UnAuthorized("No 'hash' parameter in request!"));
        if (!session.demand(VALIDATOR).validate(user, hash)) {
            throw new SessionValidationException.UnAuthorized("Invalid request digest!");
        }
    }

    @Nullable
    private VkUser getParamUser() {
        final Integer uid = PARAM_UID.getValue(req);
        if (uid == null) {
            return null;
        }
        final VkUser.Builder userBuilder = new VkUser.Builder(uid);
        if (!Optionals.ifPresent(PARAM_FIRST_NAME.getValue(req), userBuilder::firstName)) {
            return null;
        }
        if (!Optionals.ifPresent(PARAM_LAST_NAME.getValue(req), userBuilder::lastName)) {
            return null;
        }
        if (!Optionals.ifPresent(PARAM_PHOTO.getValue(req), userBuilder::photo)) {
            return null;
        }
        if (!Optionals.ifPresent(PARAM_PHOTO_REC.getValue(req), userBuilder::photoRec)) {
            return null;
        }
        return userBuilder.build();
    }
}
