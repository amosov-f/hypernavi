package ru.hypernavi.core.telegram;

import com.google.common.base.Splitter;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.HttpGet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.hypernavi.commons.Image;
import ru.hypernavi.commons.Index;
import ru.hypernavi.commons.SearchResponse;
import ru.hypernavi.commons.Site;
import ru.hypernavi.commons.hint.Hint;
import ru.hypernavi.commons.hint.Picture;
import ru.hypernavi.commons.hint.Plan;
import ru.hypernavi.core.http.HttpClient;
import ru.hypernavi.core.http.URIBuilder;
import ru.hypernavi.core.telegram.api.Message;
import ru.hypernavi.core.telegram.api.TelegramApi;
import ru.hypernavi.core.telegram.api.Update;
import ru.hypernavi.core.telegram.api.entity.BotCommand;
import ru.hypernavi.core.telegram.api.inline.InlineQuery;
import ru.hypernavi.core.telegram.api.inline.InlineQueryResult;
import ru.hypernavi.core.telegram.api.inline.InlineQueryResultPhoto;
import ru.hypernavi.core.telegram.api.markup.KeyboardButton;
import ru.hypernavi.core.telegram.api.markup.ReplyKeyboardMarkup;
import ru.hypernavi.core.telegram.api.markup.ReplyMarkup;
import ru.hypernavi.core.telegram.update.UpdatesSource;
import ru.hypernavi.util.ArrayGeoPoint;
import ru.hypernavi.util.GeoPoint;
import ru.hypernavi.util.MoreReflectionUtils;
import ru.hypernavi.util.concurrent.LoggingThreadFactory;
import ru.hypernavi.util.json.GsonUtils;
import ru.hypernavi.util.stream.MoreStreamSupport;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    @Inject
    private TelegramApi api;
    @Inject
    @Named("hypernavi.telegram.bot.search_host")
    private String searchHost;
    @Inject
    private HttpClient httpClient;

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
            service.submit(() -> {
                try {
                    LOG.debug("Update processing started: '" + update + "'");
                    processUpdate(update);
                } catch (RuntimeException e) {
                    LOG.error("Error while processing update: '" + update + "'", e);
                }
            });
        }
    }

    private void processUpdate(@NotNull final Update update) {
        Optional.ofNullable(update.getInlineQuery()).ifPresent(this::processInlineQuery);
        Optional.ofNullable(update.getMessage()).ifPresent(this::processMessage);
    }

    private void processInlineQuery(@NotNull final InlineQuery inlineQuery) {
        final GeoPoint location = inlineQuery.getLocation();
        final String query = toNullIfBlank(inlineQuery.getQuery());
        if (location == null && query == null) {
            return;
        }
        final SearchResponse searchResponse = search(location, query);
        if (searchResponse == null) {
            LOG.error("Server not respond by: location=" + location + ", query=" + query);
            return;
        }
        final InlineQueryResult[] results = searchResponse.getData().getSites().stream()
                .map(Index::get)
                .map(Site::getHints)
                .flatMap(Arrays::stream)
                .filter(Picture.class::isInstance)
                .map(Picture.class::cast)
                .map(Picture::getImage)
                .map(image -> new InlineQueryResultPhoto(generateId(image), image.getLink(), image.getThumbOrFull()))
                .toArray(InlineQueryResult[]::new);
        api.answerInlineQuery(inlineQuery.getId(), results);
    }

    @NotNull
    private String generateId(@NotNull final Image image) {
        return String.valueOf(image.getLink().hashCode());
    }

    private void processMessage(@NotNull final Message message) {
        final int chatId = message.getChat().getId();
        GeoPoint location = message.getLocation();
        final String text = toNullIfBlank(message.getText());
        final boolean startCommand = MoreStreamSupport.instances(message.getEntities(), BotCommand.class)
                .anyMatch(command -> command.getCommand(Objects.requireNonNull(text)).equals("/start"));
        if (startCommand) {
            final KeyboardButton button = new KeyboardButton("Send location", false, true);
            final ReplyMarkup replyMarkup = new ReplyKeyboardMarkup(new KeyboardButton[]{button});
            api.sendMessage(chatId, "Hello! Send me your location and I'll show relevant map near", replyMarkup);
            return;
        }
        if (location == null && text == null) {
            return;
        }
        if (text != null) {
            final GeoPoint locationFromText = extractLocation(text);
            if (locationFromText != null) {
                location = locationFromText;
            }
        }
        final SearchResponse searchResponse = search(location, text);
        if (searchResponse == null) {
            LOG.error("Server not respond by: location=" + location + ", text=" + text);
            api.sendMessage(chatId, "Sorry, our server didn't respond to this request");
            return;
        }
        final GeoPoint finalLocation = location;
        searchResponse.getData().getSites().stream()
                .map(Index::get)
                .forEach(site -> respond(chatId, site, finalLocation));

    }

    private void respond(final int chatId, @NotNull final Site site, @Nullable final GeoPoint location) {
        final String siteName = site.getPlace().getName();
        for (final Hint hint : site.getHints()) {
            final String hintDesc = hint.getDescription();
            if (hint instanceof Plan) {
                final Plan plan = (Plan) hint;
                if (location != null) {
                    final LocationImage locationImage = LocationMapper.INSTANCE.mapLocation(plan, location);
                    if (locationImage != null) {
                        if (locationImage.isLocationInsideMap()) {
                            api.sendMessage(chatId, "You are at " + siteName + ". See your location at this place:");
                        } else {
                            api.sendMessage(chatId, "Nearest popular place is " + siteName + ". See map of this place:");
                        }
                        final Image.Format format = locationImage.getFormat();
                        api.sendPhoto(chatId, locationImage.getMap(), format, hintDesc);
                        continue;
                    }
                }
            }
            if (hint instanceof Picture) {
                api.sendPhoto(chatId, ((Picture) hint).getImage(), hintDesc);
            }
        }
    }

    @Nullable
    private SearchResponse search(@Nullable final GeoPoint location, @Nullable final String text) {
        if (location == null && text == null) {
            throw new IllegalStateException("Specify text or location!");
        }
        final URI uri = new URIBuilder("http://" + searchHost + "/search")
                .addIfNotNull("lon", location != null ? location.getLongitude() : null)
                .addIfNotNull("lat", location != null ? location.getLatitude() : null)
                .addIfNotNull("text", text)
                .add("ns", 1)
                .build();
        return httpClient.execute(new HttpGet(uri), SearchResponse.class, GsonUtils.gson());
    }

    @Nullable
    private String toNullIfBlank(@Nullable final String text) {
        return !StringUtils.isBlank(text) ? text : null;
    }

    @Nullable
    private GeoPoint extractLocation(@NotNull final String text) {
        final List<String> parts = Splitter.on(',').trimResults().splitToList(text);
        if (parts.size() != 2) {
            return null;
        }
        try {
            final double latitude = Double.parseDouble(parts.get(0));
            final double longitude = Double.parseDouble(parts.get(1));
            return ArrayGeoPoint.of(longitude, latitude);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }
}
