package ru.hypernavi.core.auth;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import ru.hypernavi.core.server.Platform;
import ru.hypernavi.core.session.Property;
import ru.hypernavi.core.session.Session;
import ru.hypernavi.core.session.SessionImpl;
import ru.hypernavi.core.session.SessionValidationException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by amosov-f on 26.06.16.
 */
public class VkRequiredAuthRequestReaderTest {
    @Test
    public void vkCookiesMustExpiredInTwoYears() throws Exception {
        final HttpServletRequest req = Mockito.mock(HttpServletRequest.class);
        Mockito.when(req.getRequestURL()).thenReturn(new StringBuffer("pizza"));
        Mockito.when(req.getRequestURI()).thenReturn("pasta");
        Mockito.when(req.getParameter("uid")).thenReturn("1");
        Mockito.when(req.getParameter("first_name")).thenReturn("first_name");
        Mockito.when(req.getParameter("last_name")).thenReturn("last_name");
        Mockito.when(req.getParameter("photo")).thenReturn("photo");
        Mockito.when(req.getParameter("photo_rec")).thenReturn("photo_rec");
        Mockito.when(req.getParameter("hash")).thenReturn("hash");
        final VkRequiredAuthRequestReader reader = new VkRequiredAuthRequestReader(req);
        final Session session = new SessionImpl();
        session.set(Property.PLATFORM, Platform.PRODUCTION);
        reader.initialize(session);
        try {
            reader.validate(session);
        } catch (SessionValidationException.Redirect e) {
            final Map<String, Cookie> cookies = Arrays.stream(e.getCookies())
                    .collect(Collectors.toMap(Cookie::getName, Function.identity()));
            final int maxAge = cookies.get("vk_user").getMaxAge();
            final long timestamp = session.getTimestamp();
            Assert.assertEquals(2, (maxAge - timestamp / 1000.0) / (60 * 60 * 24 * 365), 0.001);
            return;
        }
        Assert.fail("Must be redirect!");
    }
}