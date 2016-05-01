package ru.hypernavi.core.telegram.update;

import org.jetbrains.annotations.NotNull;


import ru.hypernavi.core.telegram.api.Update;

/**
 * User: amosov-f
 * Date: 01.05.16
 * Time: 23:42
 */
public interface UpdatesSource {
    @NotNull
    Update next();
}
