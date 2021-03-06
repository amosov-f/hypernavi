package ru.hypernavi.core.telegram.update;

import org.jetbrains.annotations.NotNull;


import com.google.inject.Singleton;
import ru.hypernavi.core.telegram.api.Update;

/**
 * User: amosov-f
 * Date: 02.05.16
 * Time: 0:02
 */
@Singleton
public final class WebhookUpdatesSource extends QueuedUpdatesSource {
    @Override
    public void add(@NotNull final Update update) {
        super.add(update);
    }
}
