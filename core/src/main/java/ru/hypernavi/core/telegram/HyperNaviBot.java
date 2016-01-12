package ru.hypernavi.core.telegram;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Objects;
import java.util.Optional;
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
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import ru.hypernavi.commons.*;
import ru.hypernavi.core.http.HyperHttpClient;
import ru.hypernavi.core.http.URIBuilder;
import ru.hypernavi.util.GeoPoint;
import ru.hypernavi.util.GeoPointImpl;
import ru.hypernavi.util.MoreIOUtils;
import ru.hypernavi.util.MoreReflectionUtils;
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

    static {
        MoreReflectionUtils.load(Index.class);
        MoreReflectionUtils.load(Site.class);
    }

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
            final GeoPoint location = message.getLocation();
            final String text = message.getText();
            service.submit(() -> {
                final SearchResponse searchResponse;
                if (location != null) {
                    searchResponse = search(location);
                    if (searchResponse == null) {
                        sendMessage(chatId, "Простите, наш сервер не работает");
                        return;
                    }
                } else if (StringUtils.startsWithIgnoreCase(text, "поиск")) {
                    searchResponse = search(Objects.requireNonNull(StringUtils.removeStartIgnoreCase(text, "поиск")).trim());
                    if (searchResponse == null) {
                        sendMessage(chatId, "Простите, я не знаю такого места");
                        return;
                    }
                } else {
                    sendMessage(chatId, "Здравствуйте! Отправьте мне свою геопозицию \uD83D\uDCCE, и я покажу ближайший к Вам гипермаркет.");
                    return;
                }
                searchResponse.getData().getSites().stream().map(Index::get).forEach(site -> respond(chatId, site));
            });
        }
    }

    private void respond(final int chatId, @NotNull final Site site) {
        sendMessage(chatId, "Адрес: " + site.getPlace().getAddress());
        for (final Hint hint : site.getHints()) {
            sendPhoto(chatId, ((Picture) hint).getImage(), hint.getDescription());
        }
    }

    @Nullable
    private GetUpdatesResponse getUpdates(final int updateId) {
        final URI uri = new URIBuilder(getMethodUrl("/getUpdates")).addParameter("offset", updateId + 1).build();
        return execute(new HttpGet(uri), GetUpdatesResponse.class);
    }

    @Nullable
    private SearchResponse search(@NotNull final GeoPoint location) {
        final URI uri = new URIBuilder("http://" + searchHost + "/search")
                .addParameter("lon", location.getLongitude())
                .addParameter("lat", location.getLatitude())
                .addParameter("ns", 1)
                .build();
        return execute(new HttpGet(uri), SearchResponse.class);
    }

    @Nullable
    private SearchResponse search(@NotNull final String text) {
        final URI uri = new URIBuilder("http://" + searchHost + "/search")
                .addParameter("text", text)
                .addParameter("ns", 1)
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

    private void sendPhoto(final int chatId, @NotNull final Image photo, @Nullable final String caption) {
        final URI uri = new URIBuilder(getMethodUrl("/sendPhoto"))
                .setParameter("chat_id", chatId)
                .setParameterIfNotNull("caption", caption)
                .build();
        final HttpEntityEnclosingRequestBase req = new HttpPost(uri);
        final String mimeType = Optional.ofNullable(photo.getFormat()).orElse(Image.Format.JPG).getMimeType();
        final InputStream photoInputStream;
        try {
            photoInputStream = MoreIOUtils.connect(photo.getLink());
        } catch (IOException e) {
            LOG.error("Can't download photo!", e);
            sendMessage(chatId, "Простите, не могу загрузить изображение");
            return;
        }
        final HttpEntity entity = MultipartEntityBuilder.create()
                .addBinaryBody("photo", photoInputStream, ContentType.create(mimeType), getFileName(photo))
                .build();
        req.setEntity(entity);
        execute(req, Object.class);
    }

    @NotNull
    private String getFileName(@NotNull final Image image) {
        final Image.Format format = Optional.ofNullable(image.getFormat()).orElse(Image.Format.JPG);
        return DigestUtils.md5Hex(image.getLink()) + "." + format.getExtension();
    }

    @NotNull
    private String getMethodUrl(@NotNull final String method) {
        return TELEGRAM_API_URL + authToken + method;
    }

    @Nullable
    private <T> T execute(@NotNull final HttpUriRequest req, @NotNull final Class<T> clazz) {
        return httpClient.executeText(req, MoreGsonUtils.parser(gson, clazz));
    }
}
