package ru.hypernavi.core.database;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONException;
import org.json.JSONObject;
import ru.hypernavi.commons.Building;
import ru.hypernavi.core.classify.scheme.Picture;
import ru.hypernavi.core.webutil.GeocoderParser;
import ru.hypernavi.util.GeoPoint;
import ru.hypernavi.util.MoreIOUtils;

/**
 * Created by Константин on 16.09.2015.
 */
public final class AddHypermarkets {
    private static final Log LOG = LogFactory.getLog(AddHypermarkets.class);


    @NotNull
    private static final HttpClient client = HttpClientBuilder.create()
            .disableContentCompression()
            .setMaxConnPerRoute(100)
            .setMaxConnTotal(100)
            .build();


    private static final int MAX_OKEY_ID = 148;
    private static final int OKEY_SCHEMA = 1;

    private AddHypermarkets() {
    }


    public static void main(@NotNull final String... args) {
        final List<List<String>> urls = new ArrayList<>();
        final List<GeoPoint> locations = new ArrayList<>();
        final List<String> pages = new ArrayList<>();
        final List<String> addresses = new ArrayList<>();


        final Picture[] newPictures = Picture.downloadFromFile("urls.txt");
        final Map<URL, Boolean> isScheme = new HashMap<>();
        for (final Picture picture : newPictures) {
            isScheme.put(picture.getUrl(), picture.getChain() != null);
        }

        for (int i = 0; i < MAX_OKEY_ID; i++) {
            final String page = "http://www.okmarket.ru/stores/store/" + i + "/";
            pages.add(page);

            final byte[] data;
            try {
                data = MoreIOUtils.read(page);
            } catch (IOException e) {
                LOG.warn(e.getMessage());
                locations.add(null);
                urls.add(null);
                continue;
            }
            final String html = new String(data, StandardCharsets.UTF_8);
            locations.add(position(html));
            urls.add(urls(html));
        }
        for (int j = 0; j < MAX_OKEY_ID; ++j) {
            addresses.add(getAddress(locations.get(j)));
        }

        /*final SchemeClassifier classifier;
        try {
            classifier = SchemeClassifier.getClassifier();
        } catch (Exception e) {
            LOG.warn(e.getMessage());
            return;
        }*/
        // not add many times
        for (int j = 0; j < MAX_OKEY_ID; ++j) {
            final List<String> url = urls.get(j);
            if (url != null && url.size() > 0) {
                final List<String> schemas = new ArrayList<>();
                for (int i = 0; i < url.size(); ++i) {
                    final Picture picture = Objects.requireNonNull(Picture.download(url.get(i)));
                    //final int type = classifier.classify(picture);
                    if (isScheme.get(picture.getUrl())) {
                        schemas.add(url.get(i));
                    }
                }
                if (schemas.size() > 1) {
                    LOG.warn("Many schemas in page:" + pages.get(j));
                } else if (schemas.size() == 1) {
                    RegisterHypermarket.register(new Building(locations.get(j), addresses.get(j)), "Okey", schemas.get(0), pages.get(j));
                }
            }
        }
    }


    private static String getAddress(@Nullable final GeoPoint loc) {
        if (loc == null) {
            return "default";
        }

        HttpResponse geocode;
        try {
            final URI path = new URIBuilder()
                    .setScheme("https")
                    .setHost("geocode-maps.yandex.ru")
                    .setPath("/1.x")
                    .addParameter("geocode", loc.getLongitude() + "," + loc.getLatitude())
                    .addParameter("format", "json")
                    .build();
            geocode = client.execute(new HttpGet(path));
        } catch (IOException | URISyntaxException ignored) {
            geocode = null;
        }
        if (geocode == null) {
            return "default";
        }

        String hypermarketsJSON;
        try {
            hypermarketsJSON = IOUtils.toString(geocode.getEntity().getContent(), StandardCharsets.UTF_8.name());
        } catch (IOException ignored) {
            hypermarketsJSON = null;
        }
        if (hypermarketsJSON == null) {
            return "default";
        }

        final GeocoderParser parser;
        try {
            parser = new GeocoderParser(new JSONObject(hypermarketsJSON));
        } catch (JSONException ignored) {
            return "default";
        }

        return parser.getAddress();
    }

    @NotNull
    private static List<String> urls(@NotNull final String html) {
        final Pattern pattern = Pattern.compile("<a href=\"([^\"]*)\"\\s+class=\"fancybox\"");
        final Matcher matcher = pattern.matcher(html);
        final List<String> urls = new ArrayList<>();
        while (matcher.find()) {
            urls.add("http://www.okmarket.ru" + matcher.group(1));
        }
        return urls;
    }

    @NotNull
    private static GeoPoint position(@NotNull final String html) {
        final Pattern pattern = Pattern.compile("new ymaps.Placemark\\(\\[([^\\]]+)\\]");
        final Matcher matcher = pattern.matcher(html);
        matcher.find();
        final String[] parts = matcher.group(1).split("\\s*,\\s*");
        return new GeoPoint(Double.parseDouble(parts[1]), Double.parseDouble(parts[0]));
    }
}
