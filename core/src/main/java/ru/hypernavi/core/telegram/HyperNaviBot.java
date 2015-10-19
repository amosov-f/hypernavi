package ru.hypernavi.core.telegram;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import ru.hypernavi.commons.Hypermarket;
import ru.hypernavi.util.GeoPoint;
import ru.hypernavi.util.MoreIOUtils;

/**
 * Created by amosov-f on 17.10.15.
 */
public final class HyperNaviBot {
    private static final Log LOG = LogFactory.getLog(HyperNaviBot.class);

    private static final String TELEGRAM_API_URL = "https://api.telegram.org/bot147907463:AAHOtdiREu8lGaqklAtidkfqaYD9XhAXhwc";
    private static final long GET_UPDATES_DELAY = 3000;

    private static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    @NotNull
    private final AtomicInteger updateId = new AtomicInteger();

    @NotNull
    private final ExecutorService service = Executors.newCachedThreadPool();
    @NotNull
    private final HttpClient httpClient = HttpClientBuilder.create().build();

    public void start() {
        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(() -> {
            try {
                processUpdates();
            } catch (IOException | RuntimeException e) {
                LOG.error("Error occured during processing updates", e);
            }
        }, 0, GET_UPDATES_DELAY, TimeUnit.MILLISECONDS);
    }

    public void processUpdates() throws IOException {
        final int updateIdSnapshot = updateId.get();
        for (final Update update : getUpdates(updateIdSnapshot).getResult()) {
            if (update.getUpdateId() > updateIdSnapshot) {
                updateId.compareAndSet(updateIdSnapshot, update.getUpdateId());
            }
            final Message message = update.getMessage();
            if (message == null) {
                continue;
            }
            final GeoPoint location = message.getLocation();
            service.submit(() -> {
                try {
                    if (location != null) {
                        final Hypermarket hypermarket = search(location).getData().getHypermarkets()[0];
                        sendMessage(message.getChat().getId(), "Ближайший гипермаркет: " + hypermarket.getAddress());
                        sendMessage(message.getChat().getId(), "Сейчас покажу его схему...");
                        sendPhoto(message.getChat().getId(), hypermarket.getUrl());
                    } else {
                        sendMessage(message.getChat().getId(), "Здравствуйте! Отправьте мне свою геопозицию \uD83D\uDCCE, и я покажу ближайший к Вам гипермаркет.");
                    }
                } catch (IOException | RuntimeException e) {
                    LOG.error("Error occured during sending request", e);
                }
            });
        }
    }

    @NotNull
    private GetUpdatesResponse getUpdates(final int updateId) throws IOException {
        final URI uri;
        try {
            uri = new URIBuilder(TELEGRAM_API_URL + "/getUpdates").addParameter("offset", String.valueOf(updateId + 1)).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return execute(new HttpGet(uri), GetUpdatesResponse.class);
    }

    @NotNull
    private SearchResponse search(@NotNull final GeoPoint location) throws IOException {
        final URI uri;
        try {
            uri = new URIBuilder("http://hypernavi.net/schemainfo")
                    .addParameter("lon", String.valueOf(location.getLongitude()))
                    .addParameter("lat", String.valueOf(location.getLatitude()))
                    .build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return execute(new HttpGet(uri), SearchResponse.class);
    }

    private void sendMessage(final int chatId, @NotNull final String text) throws IOException {
        final URI uri;
        try {
            uri = new URIBuilder(TELEGRAM_API_URL + "/sendMessage")
                    .addParameter("chat_id", String.valueOf(chatId))
                    .addParameter("text", text)
                    .build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        execute(new HttpGet(uri), Object.class);
    }

    private void sendPhoto(final int chatId, @NotNull final String photoUrl) throws IOException {
        final URI uri;
        try {
            uri = new URIBuilder(TELEGRAM_API_URL + "/sendPhoto").setParameter("chat_id", String.valueOf(chatId)).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        final HttpEntityEnclosingRequestBase req = new HttpPost(uri);
        req.setEntity(MultipartEntityBuilder.create()
                .addBinaryBody("photo", MoreIOUtils.connect(photoUrl), ContentType.create("image/jpeg"), photoUrl)
                .build());
        execute(req, Object.class);
    }

    @NotNull
    private <T> T execute(@NotNull final HttpUriRequest req, @NotNull final Class<T> clazz) throws IOException {
        LOG.info("Telegram: " + req.getURI());
        final HttpResponse resp = httpClient.execute(req);
        final HttpEntity entity = resp.getEntity();
        final T result = GSON.fromJson(new InputStreamReader(entity.getContent(), StandardCharsets.UTF_8), clazz);
        EntityUtils.consumeQuietly(entity);
        return result;
    }

    public static void main(@NotNull final String[] args) {
        new HyperNaviBot().start();
    }
}
