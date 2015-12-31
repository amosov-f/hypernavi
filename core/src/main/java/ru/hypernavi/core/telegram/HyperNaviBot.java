package ru.hypernavi.core.telegram;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializer;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import ru.hypernavi.commons.Hypermarket;
import ru.hypernavi.core.http.HyperHttpClient;
import ru.hypernavi.core.http.URIBuilder;
import ru.hypernavi.util.GeoPoint;
import ru.hypernavi.util.GeoPointImpl;
import ru.hypernavi.util.MoreIOUtils;
import ru.hypernavi.util.concurrent.LoggingThreadFactory;
import ru.hypernavi.util.concurrent.MoreExecutors;
import ru.hypernavi.util.json.GsonUtils;
import ru.hypernavi.util.json.MoreGsonUtils;

/**
 * Created by amosov-f on 17.10.15.
 */
public final class HyperNaviBot {
    private static final Log LOG = LogFactory.getLog(HyperNaviBot.class);

    private static final String TELEGRAM_API_URL = "https://api.telegram.org/bot";
    private static final long GET_UPDATES_DELAY = 3000;

    @NotNull
    private final AtomicInteger updateId = new AtomicInteger();

    @NotNull
    private final ExecutorService service = Executors.newCachedThreadPool(new LoggingThreadFactory("SENDER"));

    @NotNull
    private final String searchHost;
    @NotNull
    private final HyperHttpClient httpClient;

    @NotNull
    private final String authToken;

    @NotNull
    private final Gson gson;

    @Inject
    public HyperNaviBot(@Named("hypernavi.telegram.bot.auth_token") @NotNull final String authToken,
                        @Named("hypernavi.telegram.bot.search_host") @NotNull final String searchHost,
                        @NotNull final HyperHttpClient httpClient)
    {
        this.authToken = authToken;
        this.searchHost = searchHost;
        this.httpClient = httpClient;
        gson = GsonUtils.builder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(GeoPoint.class, (JsonDeserializer<GeoPoint>) (json, t, context) -> {
                    return context.deserialize(json, GeoPointImpl.class);
                })
                .create();
    }

    public void start(final boolean inBackground) {
        final ScheduledThreadPoolExecutor executor = MoreExecutors.newSingleThreadScheduledExecutor("BOT", inBackground);
        executor.scheduleWithFixedDelay(this::processUpdates, 0, GET_UPDATES_DELAY, TimeUnit.MILLISECONDS);
    }

    public void processUpdates() {
        final int updateIdSnapshot = updateId.get();
        final GetUpdatesResponse getUpdatesResponse = getUpdates(updateIdSnapshot);
        if (getUpdatesResponse == null) {
            return;
        }
        for (final Update update : getUpdatesResponse.getResult()) {
            if (update.getUpdateId() > updateIdSnapshot) {
                updateId.compareAndSet(updateIdSnapshot, update.getUpdateId());
            }
            final Message message = update.getMessage();
            if (message == null) {
                continue;
            }
            final int chatId = message.getChat().getId();
            final GeoPointImpl location = message.getLocation();
            service.submit(() -> {
                if (location == null) {
                    sendMessage(chatId, "Здравствуйте! Отправьте мне свою геопозицию \uD83D\uDCCE, и я покажу ближайший к Вам гипермаркет.");
                    return;
                }
                final SearchResponse searchResponse = search(location);
                if (searchResponse == null) {
                    sendMessage(chatId, "Простите, наш сервер не работает");
                    return;
                }
                final Hypermarket hypermarket = searchResponse.getData().getHypermarkets()[0];
                sendMessage(chatId, "Ближайший гипермаркет: " + hypermarket.getAddress());
                sendMessage(chatId, "Сейчас покажу его схему...");
                sendPhoto(chatId, "http://" + searchHost + hypermarket.getPath());
            });
        }
    }

    @Nullable
    private GetUpdatesResponse getUpdates(final int updateId) {
        final URI uri = new URIBuilder(getMethodUrl("/getUpdates")).addParameter("offset", updateId + 1).build();
        return execute(new HttpGet(uri), GetUpdatesResponse.class);
    }

    @Nullable
    private SearchResponse search(@NotNull final GeoPointImpl location) {
        final URI uri = new URIBuilder("http://" + searchHost + "/schemainfo")
                .addParameter("lon", location.getLongitude())
                .addParameter("lat", location.getLatitude())
                .build();
        return execute(new HttpGet(uri), SearchResponse.class);
    }

    private void sendMessage(final int chatId, @NotNull final String text) {
        final URI uri = new URIBuilder(getMethodUrl("/sendMessage"))
                .addParameter("chat_id", chatId)
                .addParameter("text", text)
                .build();
        execute(new HttpGet(uri), Object.class);
    }

    private void sendPhoto(final int chatId, @NotNull final String photoUrl) {
        final URI uri = new URIBuilder(getMethodUrl("/sendPhoto")).setParameter("chat_id", chatId).build();
        final HttpEntityEnclosingRequestBase req = new HttpPost(uri);
        try {
            req.setEntity(MultipartEntityBuilder.create()
                    .addBinaryBody("photo", MoreIOUtils.connect(photoUrl), ContentType.create("image/jpeg"), photoUrl)
                    .build());
        } catch (IOException e) {
            LOG.error("Can't download photo!", e);
        }
        execute(req, Object.class);
    }

    @NotNull
    private String getMethodUrl(@NotNull final String method) {
        return TELEGRAM_API_URL + authToken + method;
    }

    @Nullable
    private <T> T execute(@NotNull final HttpUriRequest req, @NotNull final Class<T> clazz) {
        return httpClient.executeText(req, MoreGsonUtils.parser(gson, clazz));
    }

    public static void main(@NotNull final String... args) {
        new HyperNaviBot(
                "139192271:AAGD6kiaoiiJEBxnquWOdu2WTVLisAqAWPE",
                "hypernavi.net",
                new HyperHttpClient(HttpClientBuilder.create().build())
        ).start(false);
    }
}
