package ru.hypernavi.core.telegram.update;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import ru.hypernavi.core.telegram.api.GetUpdatesResponse;
import ru.hypernavi.core.telegram.api.TelegramApi;
import ru.hypernavi.core.telegram.api.Update;
import ru.hypernavi.util.concurrent.MoreExecutors;

import java.util.Arrays;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * User: amosov-f
 * Date: 01.05.16
 * Time: 23:45
 */
@Singleton
public final class GetUpdatesSource extends QueuedUpdatesSource {
    private static final long GET_UPDATES_DELAY = 3000;

    @NotNull
    private final TelegramApi api;

    @NotNull
    private final AtomicInteger updateId = new AtomicInteger();

    @Inject
    public GetUpdatesSource(@NotNull final TelegramApi api) {
        this.api = api;
        final ScheduledThreadPoolExecutor executor = MoreExecutors.newSingleThreadScheduledExecutor("GET_UPDATES", true);
        executor.scheduleWithFixedDelay(this::getUpdates, 0, GET_UPDATES_DELAY, TimeUnit.MILLISECONDS);
    }

    public void getUpdates() {
        final int updateIdSnapshot = updateId.get();
        final GetUpdatesResponse getUpdatesResponse = api.getUpdates(updateIdSnapshot);
        if (getUpdatesResponse == null) {
            return;
        }

        final long receiptTimestamp = System.currentTimeMillis();
        Arrays.stream(getUpdatesResponse.getResult()).forEach(update -> update.setReceiptTimestamp(receiptTimestamp));

        final IntStream updateIdsStream = Arrays.stream(getUpdatesResponse.getResult()).mapToInt(Update::getUpdateId);
        final int maxUpdateId = IntStream.concat(IntStream.of(updateIdSnapshot), updateIdsStream).max().orElse(updateIdSnapshot);
        updateId.compareAndSet(updateIdSnapshot, maxUpdateId);

        Arrays.stream(getUpdatesResponse.getResult()).forEach(this::add);
    }
}
