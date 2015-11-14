package ru.hypernavi.core.auth;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;


import ru.hypernavi.core.session.*;
import ru.hypernavi.util.function.Optionals;

import static ru.hypernavi.core.session.SessionInitializationException.Error.*;

/**
 * Created by amosov-f on 14.11.15.
 */
public class VkAuthRequestReader extends RequestReader {
    private static final Property<String> HASH = new Property<>("vk_hash");

    private static final RequestParam<Integer> PARAM_UID = new RequestParam.IntegerParam("uid");
    private static final RequestParam<String> PARAM_FIRST_NAME = new RequestParam.StringParam("first_name");
    private static final RequestParam<String> PARAM_LAST_NAME = new RequestParam.StringParam("last_name");
    private static final RequestParam<String> PARAM_PHOTO = new RequestParam.StringParam("photo");
    private static final RequestParam<String> PARAM_PHOTO_REC = new RequestParam.StringParam("photo_rec");
    private static final RequestParam<String> PARAM_HASH = new RequestParam.StringParam("hash");

    @NotNull
    private final VkAuthValidator validator;

    public VkAuthRequestReader(@NotNull final HttpServletRequest req, @NotNull final VkAuthValidator validator) {
        super(req);
        this.validator = validator;
    }

    @Override
    public void initialize(@NotNull final Session session) {
        super.initialize(session);

        session.setIfNotNull(Property.VK_USER, getUser());
        setPropertyIfPresent(session, HASH, PARAM_HASH);
    }

    @Override
    public void validate(@NotNull final Session session) throws SessionInitializationException {
        super.validate(session);
        final VkUser user = Optional.ofNullable(session.get(Property.VK_USER))
                .orElseThrow(() -> new SessionInitializationException(UNAUTHORIZED, "Not enough vk auth parameters!"));
        final String hash = Optional.ofNullable(session.get(HASH))
                .orElseThrow(() -> new SessionInitializationException(UNAUTHORIZED, "No 'hash' parameter in request!"));
        if (!validator.validate(user, hash)) {
            throw new SessionInitializationException(UNAUTHORIZED, "Invalid request digest!");
        }
    }

    @Nullable
    private VkUser getUser() {
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
