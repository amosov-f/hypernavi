package ru.hypernavi.core.telegram;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.HttpGet;
import ru.hypernavi.commons.Image;
import ru.hypernavi.commons.*;
import ru.hypernavi.commons.hint.Hint;
import ru.hypernavi.commons.hint.Picture;
import ru.hypernavi.commons.hint.Plan;
import ru.hypernavi.core.http.HyperHttpClient;
import ru.hypernavi.core.http.URIBuilder;
import ru.hypernavi.core.telegram.api.GetUpdatesResponse;
import ru.hypernavi.core.telegram.api.Message;
import ru.hypernavi.core.telegram.api.TelegramApi;
import ru.hypernavi.core.telegram.api.Update;
import ru.hypernavi.core.telegram.api.inline.InlineQuery;
import ru.hypernavi.core.telegram.api.inline.InlineQueryResult;
import ru.hypernavi.core.telegram.api.inline.InlineQueryResultPhoto;
import ru.hypernavi.core.telegram.api.markup.KeyboardButton;
import ru.hypernavi.core.telegram.api.markup.ReplyKeyboardMarkup;
import ru.hypernavi.core.telegram.api.markup.ReplyMarkup;
import ru.hypernavi.ml.regression.map.MapProjection;
import ru.hypernavi.ml.regression.map.MapProjectionImpl;
import ru.hypernavi.util.GeoPoint;
import ru.hypernavi.util.MoreIOUtils;
import ru.hypernavi.util.MoreReflectionUtils;
import ru.hypernavi.util.concurrent.LoggingThreadFactory;
import ru.hypernavi.util.concurrent.MoreExecutors;
import ru.hypernavi.util.json.GsonUtils;

/**
 * Created by amosov-f on 17.10.15.
 */
public final class HyperNaviBot {
    private static final Log LOG = LogFactory.getLog(HyperNaviBot.class);

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
    private final Gson searchGson = GsonUtils.gson();

    @Inject
    private TelegramApi api;
    @Inject
    @Named("hypernavi.telegram.bot.search_host")
    private String searchHost;
    @Inject
    private HyperHttpClient httpClient;

    public void start(final boolean inBackground) {
        final ScheduledThreadPoolExecutor executor = MoreExecutors.newSingleThreadScheduledExecutor("BOT", inBackground);
        executor.scheduleWithFixedDelay(this::processUpdates, 0, GET_UPDATES_DELAY, TimeUnit.MILLISECONDS);
    }

    public void processUpdates() {
        final int updateIdSnapshot = updateId.get();
        final GetUpdatesResponse getUpdatesResponse = api.getUpdates(updateIdSnapshot);
        if (getUpdatesResponse == null) {
            return;
        }
        for (final Update update : getUpdatesResponse.getResult()) {
            if (update.getUpdateId() > updateIdSnapshot) {
                updateId.compareAndSet(updateIdSnapshot, update.getUpdateId());
            }
            service.submit(() -> processUpdate(update));
        }
    }

    private void processUpdate(@NotNull final Update update) {
        Optional.ofNullable(update.getInlineQuery()).ifPresent(this::processInlineQuery);
        Optional.ofNullable(update.getMessage()).ifPresent(this::processMessage);
    }

    private void processInlineQuery(@NotNull final InlineQuery inlineQuery) {
        final GeoPoint location = inlineQuery.getLocation();
        final String query = inlineQuery.getQuery();
        if (location == null && !StringUtils.startsWithIgnoreCase(query, "поиск")) {
            LOG.warn("No location in request");
            return;
        }
        final SearchResponse searchResponse;
        if (location != null) {
            searchResponse = search(location);
        } else {
            searchResponse = search(StringUtils.removeStartIgnoreCase(query, "поиск").trim());
        }
        if (searchResponse == null) {
            LOG.error("Server not respond by location " + location);
            return;
        }
        final InlineQueryResult[] results = searchResponse.getData().getSites().stream()
                .map(Index::get)
                .map(Site::getHints)
                .flatMap(Arrays::stream)
                .filter(Picture.class::isInstance)
                .map(Picture.class::cast)
                .map(Picture::getImage)
                .map(Image::getLink)
                .map(link -> new InlineQueryResultPhoto(link, link, link))
                .toArray(InlineQueryResult[]::new);
        api.answerInlineQuery(inlineQuery.getId(), results);
    }

    private void processMessage(@NotNull final Message message) {
        final int chatId = message.getChat().getId();
        final GeoPoint location = message.getLocation();
        final String text = message.getText();
        try {
            final SearchResponse searchResponse;
            if (location != null) {
                searchResponse = search(location);
                if (searchResponse == null) {
                    api.sendMessage(chatId, "Простите, наш сервер не работает");
                    return;
                }
            } else if (StringUtils.startsWithIgnoreCase(text, "поиск")) {
                searchResponse = search(Objects.requireNonNull(StringUtils.removeStartIgnoreCase(text, "поиск")).trim());
                if (searchResponse == null) {
                    api.sendMessage(chatId, "Простите, я не знаю такого места");
                    return;
                }
            } else {
                final KeyboardButton button = new KeyboardButton("Отправить геопозицию", false, true);
                final ReplyMarkup replyMarkup = new ReplyKeyboardMarkup(new KeyboardButton[]{button});
                api.sendMessage(chatId, "Здравствуйте! Отправьте мне свою геопозицию, и я что-нибудь покажу =)", replyMarkup);
                return;
            }
            searchResponse.getData().getSites().stream().map(Index::get).forEach(site -> respond(chatId, site, location));
        } catch (RuntimeException e) {
            LOG.error("Error!", e);
        }
    }

    private void respond(final int chatId, @NotNull final Site site, @Nullable final GeoPoint location) {
        api.sendMessage(chatId, "Адрес: " + site.getPlace().getAddress());
        for (final Hint hint : site.getHints()) {
            if (hint instanceof Plan) {
                final Plan plan = (Plan) hint;
                if (location != null) {
                    final BufferedImage image = drawLocation(plan, location);
                    if (image != null) {
                        api.sendPhoto(chatId, image, plan.getImage().getFormat(), hint.getDescription());
                        continue;
                    }
                }
            }
            if (hint instanceof Picture) {
                api.sendPhoto(chatId, ((Picture) hint).getImage(), hint.getDescription());
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
    private SearchResponse search(@NotNull final GeoPoint location) {
        final URI uri = new URIBuilder("http://" + searchHost + "/search")
                .add("lon", location.getLongitude())
                .add("lat", location.getLatitude())
                .add("ns", 1)
                .build();
        return httpClient.execute(new HttpGet(uri), SearchResponse.class, searchGson);
    }

    @Nullable
    private SearchResponse search(@NotNull final String text) {
        final URI uri = new URIBuilder("http://" + searchHost + "/search")
                .add("text", text)
                .add("ns", 1)
                .build();
        return httpClient.execute(new HttpGet(uri), SearchResponse.class, searchGson);
    }
}
