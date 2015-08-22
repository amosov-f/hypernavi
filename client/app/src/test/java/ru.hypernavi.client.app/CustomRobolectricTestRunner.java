package ru.hypernavi.client.app;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Properties;


import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.manifest.AndroidManifest;

/**
 * Created by amosov-f on 22.08.15.
 */
public final class CustomRobolectricTestRunner extends RobolectricTestRunner {
    private static final String[] MANIFEST_PATHS = {"../app/AndroidManifest.xml", "client/app/AndroidManifest.xml"};

    public CustomRobolectricTestRunner(@NotNull final Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    protected AndroidManifest getAppManifest(@NotNull final Config config) {
        final Properties properties = new Properties();
        properties.setProperty("manifest", manifestPath());
        return super.getAppManifest(new Config.Implementation(config, Config.Implementation.fromProperties(properties)));
    }

    @NotNull
    private static String manifestPath() {
        for (final String manifestPath : MANIFEST_PATHS) {
            if (new File(manifestPath).exists()) {
                return manifestPath;
            }
        }
        throw new RuntimeException("No android manifest file found!");
    }
}
