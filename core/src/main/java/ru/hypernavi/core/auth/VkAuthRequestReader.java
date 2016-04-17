package ru.hypernavi.core.auth;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;


import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import ru.hypernavi.core.http.URIBuilder;
import ru.hypernavi.core.session.*;
import ru.hypernavi.util.function.Optionals;
import ru.hypernavi.util.json.GsonUtils;

/**
 * Created by amosov-f on 14.11.15.
 */
public class VkAuthRequestReader extends RequestReader {
    private static final RequestParam<Integer> PARAM_UID = new RequestParam.IntegerParam("uid");
    private static final RequestParam<String> PARAM_FIRST_NAME = new RequestParam.StringParam("first_name");
    private static final RequestParam<String> PARAM_LAST_NAME = new RequestParam.StringParam("last_name");
    private static final RequestParam<String> PARAM_PHOTO = new RequestParam.StringParam("photo");
    private static final RequestParam<String> PARAM_PHOTO_REC = new RequestParam.StringParam("photo_rec");
    private static final RequestParam<String> PARAM_HASH = new RequestParam.StringParam("hash");

    private static final RequestParam<?>[] VK_PARAMS = new RequestParam[]{
            PARAM_UID, PARAM_FIRST_NAME, PARAM_LAST_NAME, PARAM_PHOTO, PARAM_PHOTO_REC, PARAM_HASH
    };

    private static final RequestParam.CookieParam<VkUser> COOKIE_VK_USER = new RequestParam.ObjectCookieParam<>("vk_user", VkUser.class);
    private static final RequestParam.CookieParam<String> COOKIE_VK_HASH = new RequestParam.StringCookieParam("vk_hash");

    @Inject
    private VkAuthValidator validator;

    @Inject
    public VkAuthRequestReader(@Assisted @NotNull final HttpServletRequest req) {
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
            for (final RequestParam<?> param : VK_PARAMS) {
                builder.remove(param.getName());
            }
            final Cookie[] cookies = {
                    new Cookie(COOKIE_VK_USER.getName(), GsonUtils.gson().toJson(getParamUser())),
                    new Cookie(COOKIE_VK_HASH.getName(), PARAM_HASH.getValue(req))
            };
            Arrays.stream(cookies).forEach(cookie -> cookie.setDomain("hypernavi.net"));
            throw new SessionValidationException.Redirect("Vk params in request", builder.build().toString(), cookies);
        }
        final VkUser user = Optional.ofNullable(session.get(Property.VK_USER))
                .orElseThrow(() -> new SessionValidationException.UnAuthorized("Not enough vk auth parameters!"));
        final String hash = Optional.ofNullable(COOKIE_VK_HASH.getValue(req))
                .orElseThrow(() -> new SessionValidationException.UnAuthorized("No 'hash' parameter in request!"));
        if (!validator.validate(user, hash)) {
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
