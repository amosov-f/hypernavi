package ru.hypernavi.core.auth;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by amosov-f on 14.11.15.
 */
public final class VkAuthValidatorTest {
    private VkAuthValidator validator;

    @Before
    public void setUp() {
        validator = new VkAuthValidator(5102874, "WH6u8h0Ku0dwuGxPfNRb");
    }

    @Test
    public void test() {
        final VkUser user = new VkUser.Builder(98810985)
                .firstName("Федор")
                .lastName("Амосов")
                .photo("https://pp.vk.me/c623417/v623417985/fdac/Pkzsl78XHNk.jpg")
                .photoRec("https://pp.vk.me/c623417/v623417985/fdb1/pWtELggN_JI.jpg")
                .build();
        Assert.assertTrue(validator.validate(user, "84d00b470a6d778c5999eef4efaaedf2"));
    }
}