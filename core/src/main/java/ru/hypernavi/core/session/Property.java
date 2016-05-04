package ru.hypernavi.core.session;

import org.jetbrains.annotations.NotNull;


import ru.hypernavi.core.auth.VkUser;
import ru.hypernavi.core.server.Platform;
import ru.hypernavi.util.GeoPoint;

/**
 * Created by amosov-f on 24.10.15.
 */
public final class Property<T> {
    public static final Property<String> HTTP_REQUEST_URL = new Property<>("http_request_url");
    public static final Property<String> HTTP_REQUEST_URI = new Property<>("http_request_uri");
    public static final Property<String> HTTP_SERVLET_PATH = new Property<>("http_servlet_path");
    public static final Property<String> HTTP_PATH_INFO = new Property<>("http_path_info");
    public static final Property<String> HTTP_QUERY_STRING = new Property<>("http_query_string");
    public static final Property<String> HTTP_COOKIE = new Property<>("http_cookie");
    public static final Property<byte[]> HTTP_BODY = new Property<>("http_body");
    public static final Property<String> HTTP_BODY_UTF8 = new Property<>("http_body_utf8");

    public static final Property<Platform> PLATFORM = new Property<>("platform");

    public static final Property<String> TEXT = new Property<>("text");
    public static final Property<GeoPoint> GEO_LOCATION = new Property<>("geo_location");
    
    public static final Property<VkUser> VK_USER = new Property<>("vk_user");
    public static final Property<Boolean> DEBUG = new Property<>("debug");

    @NotNull
    private final String name;

    public Property(@NotNull final String name) {
        this.name = name;
    }

    @NotNull
    @Override
    public String toString() {
        return name;
    }
}
