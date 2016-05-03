package ru.hypernavi.core.telegram.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Optional;
import java.util.function.Supplier;


import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializer;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.apache.commons.codec.digest.DigestUtils;
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
import ru.hypernavi.core.http.HttpClient;
import ru.hypernavi.core.http.URIBuilder;
import ru.hypernavi.core.telegram.api.inline.InlineQueryResult;
import ru.hypernavi.core.telegram.api.markup.ReplyMarkup;
import ru.hypernavi.util.GeoPoint;
import ru.hypernavi.util.GeoPointImpl;
import ru.hypernavi.util.MoreIOUtils;
import ru.hypernavi.util.awt.ImageUtils;
import ru.hypernavi.util.json.GsonUtils;

/**
 * User: amosov-f
 * Date: 23.04.16
 * Time: 0:24
 */
public final class TelegramApi {
    private static final Log LOG = LogFactory.getLog(TelegramApi.class);

    private static final String TELEGRAM_API_URL = "https://api.telegram.org/bot";

    @Inject
    @Named("hypernavi.telegram.bot.auth_token")
    private String authToken;
    @Inject
    private HttpClient httpClient;

    @Nullable
    public GetUpdatesResponse getUpdates(final int updateId) {
        final URI uri = method("/getUpdates").add("offset", updateId + 1).build();
        return execute(new HttpGet(uri), GetUpdatesResponse.class);
    }

    public void sendMessage(final int chatId, @NotNull final String text) {
        sendMessage(chatId, text, null);
    }

    public void sendMessage(final int chatId, @NotNull final String text, @Nullable final ReplyMarkup replyMarkup) {
        final URI uri = method("/sendMessage")
                .add("chat_id", chatId)
                .add("text", text)
                .addIfNotNull("reply_markup", Optional.ofNullable(replyMarkup).map(gson()::toJson).orElse(null))
                .build();
        execute(new HttpGet(uri), Object.class);
    }

    public void sendPhoto(final int chatId,
                          @NotNull final BufferedImage photo,
                          @NotNull final Image.Format format,
                          @Nullable final String caption)
    {
        final byte[] photoBytes = ImageUtils.toByteArray(photo, format.getExtension());
        sendPhoto(chatId, new ByteArrayInputStream(photoBytes), format, caption);
    }

    public void sendPhoto(final int chatId, @NotNull final Image photo, @Nullable final String caption) {
        final InputStream photoStream = MoreIOUtils.connectSafe(photo.getLink());
        if (photoStream == null) {
            LOG.error("Can't download photo: " + photo.getLink());
            sendMessage(chatId, "Простите, не могу загрузить изображение");
            return;
        }
        sendPhoto(chatId, photoStream, photo.getFormat(), caption);
    }

    private void sendPhoto(final int chatId,
                           @NotNull final InputStream photoStream,
                           @Nullable final Image.Format format,
                           @Nullable final String caption)
    {
        final URI uri = method("/sendPhoto")
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

    public void answerInlineQuery(@NotNull final String inlineQueryId, @NotNull final InlineQueryResult... results) {
        final String resultsParam = gson().toJson(results);
        final URI uri = method("/answerInlineQuery")
                .set("inline_query_id", inlineQueryId)
                .set("results", resultsParam)
                .build();
        execute(new HttpGet(uri), Object.class);
    }

    @NotNull
    public static Gson gson() {
        return GsonUtils.builder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(GeoPoint.class, (JsonDeserializer<?>) (json, t, ctx) -> ctx.deserialize(json, GeoPointImpl.class))
                .create();
    }

    @NotNull
    public static Supplier<Gson> gsonFactory() {
        return TelegramApi::gson;
    }

    @NotNull
    private URIBuilder method(@NotNull final String method) {
        return new URIBuilder(TELEGRAM_API_URL + authToken + method);
    }

    @Nullable
    private <T> T execute(@NotNull final HttpUriRequest req, @NotNull final Class<T> clazz) {
        return httpClient.execute(req, clazz, gson());
    }

    @NotNull
    private String getFileName(@NotNull final InputStream in, @Nullable final Image.Format format) {
        return DigestUtils.md5Hex(in.toString()) + "." + (format != null ? format : Image.Format.JPG).getExtension();
    }
}
