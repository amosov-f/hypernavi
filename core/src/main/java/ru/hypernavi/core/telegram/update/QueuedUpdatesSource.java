package ru.hypernavi.core.telegram.update;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


import ru.hypernavi.core.telegram.api.Update;

/**
 * User: amosov-f
 * Date: 01.05.16
 * Time: 23:47
 */
public class QueuedUpdatesSource implements UpdatesSource {
    @NotNull
    private final BlockingQueue<Update> updates = new LinkedBlockingQueue<>();

    @NotNull
    @Override
    public Update next() {
        try {
            return updates.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    protected void add(@NotNull final Update update) {
        updates.add(update);
    }
}
