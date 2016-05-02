package ru.hypernavi.core.telegram;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


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
import ru.hypernavi.core.http.HttpClient;
import ru.hypernavi.core.http.URIBuilder;
import ru.hypernavi.core.telegram.api.Message;
import ru.hypernavi.core.telegram.api.TelegramApi;
import ru.hypernavi.core.telegram.api.Update;
import ru.hypernavi.core.telegram.api.inline.InlineQuery;
import ru.hypernavi.core.telegram.api.inline.InlineQueryResult;
import ru.hypernavi.core.telegram.api.inline.InlineQueryResultPhoto;
import ru.hypernavi.core.telegram.api.markup.KeyboardButton;
import ru.hypernavi.core.telegram.api.markup.ReplyKeyboardMarkup;
import ru.hypernavi.core.telegram.api.markup.ReplyMarkup;
import ru.hypernavi.core.telegram.update.UpdatesSource;
import ru.hypernavi.core.webutil.ImageEditor;
import ru.hypernavi.ml.regression.map.MapProjection;
import ru.hypernavi.ml.regression.map.MapProjectionImpl;
import ru.hypernavi.util.GeoPoint;
import ru.hypernavi.util.MoreReflectionUtils;
import ru.hypernavi.util.awt.ImageUtils;
import ru.hypernavi.util.concurrent.LoggingThreadFactory;
import ru.hypernavi.util.json.GsonUtils;

/**
 * Created by amosov-f on 17.10.15.
 */
public final class HyperNaviBot {
    private static final Log LOG = LogFactory.getLog(HyperNaviBot.class);

    static {
        MoreReflectionUtils.load(Index.class);
        MoreReflectionUtils.load(Site.class);
    }

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
    private HttpClient httpClient;
    @Inject
    private ImageEditor imageEditor;

    @Inject
    private UpdatesSource updatesSource;

    public void start(final boolean inBackground) {
        if (inBackground) {
            service.submit((Runnable) this::start);
        } else {
            start();
        }
    }

    public void start() {
        while (true) {
            LOG.info("Waiting for next update...");
            final Update update = updatesSource.next();
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
        final SearchResponse searchResponse;
        if (location != null) {
            searchResponse = search(location);
        } else {
            searchResponse = search(query);
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
                .map(Image::getThumbOrFull)
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
                        final Image.Format format = Optional.ofNullable(ImageUtils.format(image))
                                .map(Image.Format::parse)
                                .orElse(plan.getImage().getFormat(Image.Format.JPG));
                        api.sendPhoto(chatId, image, format, hint.getDescription());
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

        final BufferedImage image = ImageUtils.downloadSafe(plan.getImage().getLink());
        if (image == null) {
            LOG.warn("Can't download image: " + plan.getImage().getLink());
            return null;
        }
        return imageEditor.drawLocation(image, point);
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
