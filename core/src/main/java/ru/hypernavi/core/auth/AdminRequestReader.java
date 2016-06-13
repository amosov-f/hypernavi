package ru.hypernavi.core.auth;

import org.jetbrains.annotations.NotNull;
import ru.hypernavi.core.session.Property;
import ru.hypernavi.core.session.Session;
import ru.hypernavi.core.session.SessionValidationException;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by amosov-f on 14.11.15.
 */
public class AdminRequestReader extends VkRequiredAuthRequestReader {
    public AdminRequestReader(@NotNull final HttpServletRequest req) {
        super(req);
    }

    @Override
    public void validate(@NotNull final Session session) throws SessionValidationException {
        if (!session.get(Property.IS_ADMIN, false)) {
            super.validate(session);
            throw new SessionValidationException.Forbidden("Only admins have access to this page!");
        }
    }
}
