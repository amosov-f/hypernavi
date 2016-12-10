package ru.hypernavi.core.telegram.update;

import org.apache.log4j.MDC;
import org.jetbrains.annotations.NotNull;
import ru.hypernavi.core.session.Session;
import ru.hypernavi.core.telegram.api.Update;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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
            final Update update = updates.take();
            MDC.put(Session.REQ_ID, update.getReqId());
            return update;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    protected void add(@NotNull final Update update) {
        updates.add(update);
    }
}
