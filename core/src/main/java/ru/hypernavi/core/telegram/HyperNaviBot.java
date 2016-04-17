package ru.hypernavi.core.telegram;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import ru.hypernavi.commons.Image;
import ru.hypernavi.commons.*;
import ru.hypernavi.commons.hint.Hint;
import ru.hypernavi.commons.hint.Picture;
import ru.hypernavi.commons.hint.Plan;
import ru.hypernavi.core.http.HyperHttpClient;
import ru.hypernavi.core.http.URIBuilder;
import ru.hypernavi.ml.regression.map.MapProjection;
import ru.hypernavi.ml.regression.map.MapProjectionImpl;
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
    private final Gson telegramGson;
    @NotNull
    private final Gson searchGson;

    @Inject
    public HyperNaviBot(@Named("hypernavi.telegram.bot.auth_token") @NotNull final String authToken,
                        @Named("hypernavi.telegram.bot.search_host") @NotNull final String searchHost,
                        @NotNull final HyperHttpClient httpClient)
    {
        this.authToken = authToken;
        this.searchHost = searchHost;
        this.httpClient = httpClient;
        telegramGson = GsonUtils.builder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(GeoPoint.class, (JsonDeserializer<GeoPoint>) (json, t, context) -> {
                    return context.deserialize(json, GeoPointImpl.class);
                })
                .create();
        searchGson = GsonUtils.gson();
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
                try {
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
                    searchResponse.getData().getSites().stream().map(Index::get).forEach(site -> respond(chatId, site, location));
                } catch (RuntimeException e) {
                    LOG.error("Error!", e);
                }
            });
        }
    }

    private void respond(final int chatId, @NotNull final Site site, @Nullable final GeoPoint location) {
        sendMessage(chatId, "Адрес: " + site.getPlace().getAddress());
        for (final Hint hint : site.getHints()) {
            if (hint instanceof Plan) {
                final Plan plan = (Plan) hint;
                if (location != null) {
                    final BufferedImage image = drawLocation(plan, location);
                    if (image != null) {
                        sendPhoto(chatId, image, plan.getImage().getFormat(), hint.getDescription());
                        continue;
                    }
                }
            }
            if (hint instanceof Picture) {
                sendPhoto(chatId, ((Picture) hint).getImage(), hint.getDescription());
            }
        }
    }

    @Nullable
    private BufferedImage drawLocation(@NotNull final Plan plan, @NotNull final GeoPoint location) {
        final PointMap[] points = plan.getPoints();
        if (points.length == 0) {
            return null;
        }
        final MapProjection mapProjection = MapProjectionImpl.learn(points);
        final Point point = mapProjection.map(location);
        final BufferedImage image;
        try {
            image = ImageIO.read(MoreIOUtils.connect(plan.getImage().getLink()));
        } catch (IOException e) {
            LOG.error("Can't download image!", e);
            return null;
        }
        final Graphics2D g = (Graphics2D) image.getGraphics();
        g.setStroke(new BasicStroke(10));
        g.setColor(Color.RED);
        // TODO: size
        g.drawOval(point.x - 50, point.y - 50, 100, 100);
        g.fillOval(point.x - 7, point.y - 7, 14, 14);
        return image;
    }

    @Nullable
    private GetUpdatesResponse getUpdates(final int updateId) {
        final URI uri = new URIBuilder(getMethodUrl("/getUpdates")).add("offset", updateId + 1).build();
        return execute(new HttpGet(uri), GetUpdatesResponse.class);
    }

    @Nullable
    private SearchResponse search(@NotNull final GeoPoint location) {
        final URI uri = new URIBuilder("http://" + searchHost + "/search")
                .add("lon", location.getLongitude())
                .add("lat", location.getLatitude())
                .add("ns", 1)
                .build();
        return execute(new HttpGet(uri), SearchResponse.class, searchGson);
    }

    @Nullable
    private SearchResponse search(@NotNull final String text) {
        final URI uri = new URIBuilder("http://" + searchHost + "/search")
                .add("text", text)
                .add("ns", 1)
                .build();
        return execute(new HttpGet(uri), SearchResponse.class, searchGson);
    }

    private void sendMessage(final int chatId, @NotNull final String text) {
        final URI uri = new URIBuilder(getMethodUrl("/sendMessage"))
                .add("chat_id", chatId)
                .add("text", text)
                .build();
        execute(new HttpGet(uri), Object.class);
    }

    private void sendPhoto(final int chatId, @NotNull final BufferedImage photo, @Nullable final Image.Format format, @Nullable final String caption) {
        final ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try {
            ImageIO.write(photo, Optional.ofNullable(format).orElse(Image.Format.JPG).getExtension(), bout);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final InputStream photoStream = new ByteArrayInputStream(bout.toByteArray());
        sendPhoto(chatId, photoStream, format, caption);
    }

    private void sendPhoto(final int chatId, @NotNull final Image photo, @Nullable final String caption) {
        final InputStream photoInputStream;
        try {
            photoInputStream = MoreIOUtils.connect(photo.getLink());
        } catch (IOException e) {
            LOG.error("Can't download photo!", e);
            sendMessage(chatId, "Простите, не могу загрузить изображение");
            return;
        }
        sendPhoto(chatId, photoInputStream, photo.getFormat(), caption);
    }

    private void sendPhoto(final int chatId,
                           @NotNull final InputStream photoStream,
                           @Nullable final Image.Format format,
                           @Nullable final String caption)
    {
        final URI uri = new URIBuilder(getMethodUrl("/sendPhoto"))
                .set("chat_id", chatId)
                .setIfNotNull("caption", caption)
                .build();
        final HttpEntityEnclosingRequestBase req = new HttpPost(uri);
        final String mimeType = Optional.ofNullable(format).orElse(Image.Format.JPG).getMimeType();

        final HttpEntity entity = MultipartEntityBuilder.create()
                .addBinaryBody("photo", photoStream, ContentType.create(mimeType), getFileName(photoStream, format))
                .build();
        req.setEntity(entity);
        execute(req, Object.class);
    }

    @NotNull
    private String getFileName(@NotNull final InputStream in, @Nullable final Image.Format format) {
        return DigestUtils.md5Hex(in.toString()) + "." + (format != null ? format : Image.Format.JPG).getExtension();
    }

    @NotNull
    private String getMethodUrl(@NotNull final String method) {
        return TELEGRAM_API_URL + authToken + method;
    }

    @Nullable
    private <T> T execute(@NotNull final HttpUriRequest req, @NotNull final Class<T> clazz) {
        return execute(req, clazz, telegramGson);
    }

    @Nullable
    private <T> T execute(@NotNull final HttpUriRequest req, @NotNull final Class<T> clazz, @NotNull final Gson gson) {
        return httpClient.executeText(req, MoreGsonUtils.parser(gson, clazz));
    }
}
