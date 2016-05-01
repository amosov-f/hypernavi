package ru.hypernavi.core.telegram.update;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;


import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import ru.hypernavi.core.telegram.api.Update;
import ru.hypernavi.util.json.GsonUtils;

/**
 * User: amosov-f
 * Date: 02.05.16
 * Time: 0:20
 */
public class WebhookUpdatesSourceTest {
    @NotNull
    private final WebhookUpdatesSource source = new WebhookUpdatesSource();

    @Test
    public void updatesSourceMustBeThreadSafe() {
        final Update update = GsonUtils.gson().fromJson(read("/telegram/webhook/text.json"), Update.class);
        final ExecutorService service = Executors.newFixedThreadPool(10);
        final int[] updateIds = IntStream.range(0, 100000).toArray();
        IntStream.of(updateIds).forEach(i -> service.submit(() -> source.add(clone(update, i))));
        final int[] actualUpdateIds = IntStream.of(updateIds).parallel().map(i -> source.next().getUpdateId()).sorted().toArray();
        Assert.assertArrayEquals(updateIds, actualUpdateIds);
    }

    @NotNull
    private String read(@NotNull final String classpath) {
        try {
            return IOUtils.toString(getClass().getResourceAsStream(classpath), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private Update clone(@NotNull final Update update, final int id) {
        return new Update(id, update.getMessage(), update.getInlineQuery());
    }
}