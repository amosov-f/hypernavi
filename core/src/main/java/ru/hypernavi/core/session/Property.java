package ru.hypernavi.core.session;

import org.jetbrains.annotations.NotNull;


import ru.hypernavi.core.auth.VkUser;
import ru.hypernavi.util.GeoPoint;

/**
 * Created by amosov-f on 24.10.15.
 */
public final class Property<T> {
    public static final Property<String> HTTP_PATH_INFO = new Property<>("http_path_info");

    public static final Property<String> TEXT = new Property<>("text");
    public static final Property<GeoPoint> GEO_LOCATION = new Property<>("geo_location");
    
    public static final Property<VkUser> VK_USER = new Property<>("vk_user");

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
