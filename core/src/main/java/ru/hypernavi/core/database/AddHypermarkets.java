package ru.hypernavi.core.database;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ru.hypernavi.core.classify.scheme.Picture;
import ru.hypernavi.core.classify.scheme.SchemeClassifier;
import ru.hypernavi.util.MoreIOUtils;
/**
 * Created by Константин on 16.09.2015.
 */
public final class AddHypermarkets {
    private static final Log LOG = LogFactory.getLog(AddHypermarkets.class);

    static final List<List<String>> urls = new ArrayList<>();
  //  static final List<GeoPoint> locations = new ArrayList<>();
    static final List<String> pages = new ArrayList<>();
  //  static final List<String> addresses = new ArrayList<>();
    static final Map<URL, Boolean> isScheme = new HashMap<>();


    private static final int MAX_OKEY_ID = 148;

    private AddHypermarkets() {
    }


    public static void main(@NotNull final String... args) throws IOException {
        final Picture[] newPictures = Picture.downloadFromFile("urls.txt");

        for (final Picture picture : newPictures) {
            isScheme.put(picture.getUrl(), picture.getChain() != null);
        }

        for (int i = 0; i < MAX_OKEY_ID; i++) {
            final String page = "http://www.okmarket.ru/stores/store/" + i + "/";
            pages.add(page);
        }

        for (int i = 0; i < MAX_OKEY_ID; i++) {
            final String page = pages.get(i);
            final byte[] data;
            try {
                data = MoreIOUtils.read(page);
            } catch (IOException e) {
                urls.add(null);
                continue;
            }
            final String html = new String(data, StandardCharsets.UTF_8);
            urls.add(urls(html));
        }

//        final GeocoderParser parser = new GeocoderParser();
//
//        for (int j = 0; j < MAX_OKEY_ID; ++j) {
//            final GeoPoint p = locations.get(j);
//            if (p == null) {
//
//            } else {
//                final JSONObject geocoderResponse = GeocoderSender.getGeocoderResponse(p.getLongitude() + "," + p.getLatitude());
//                parser.setResponce(geocoderResponse);
//                final String address = (parser.getAddress() != null) ? parser.getAddress() : "default";
//                addresses.add(address);
//            }
//        }

        final SchemeClassifier classifier;
        try {
            classifier = SchemeClassifier.getClassifier();
        } catch (Exception e) {
            LOG.warn(e.getMessage());
            return;
        }


        int total = 0;
        int fail = 0;
        final int[][] matrix = new int[7][7];

        final FileOutputStream fout = new FileOutputStream(new File("data/result.txt"));

        for (int j = 0; j < MAX_OKEY_ID; ++j) {
            final List<String> url = urls.get(j);
            if (url != null && url.size() > 0) {
                for (int i = 0; i < url.size(); ++i) {
                    total++;
                    final Picture picture = Objects.requireNonNull(Picture.download(url.get(i)));
                    final int type = classifier.classify(picture);
                    final int realtype = isScheme.get(picture.getUrl()) ? 1 : 0;
                    IOUtils.write(url.get(i) + " " + type + "\n", fout);
                    if (type != realtype && (type == 1 || type == 0))
                        fail++;

                    matrix[realtype][type]++;
                }
            }
        }
        for (int i = 0; i < 7; ++i) {
            for (int j = 0; j < 7; ++j) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
        int correct = total - fail;
        System.out.println(total + " " + correct + " " + (correct * 1.0 / total));
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
}
