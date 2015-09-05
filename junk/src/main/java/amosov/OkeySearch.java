package amosov;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import ru.hypernavi.util.GeoPoint;
import ru.hypernavi.util.MoreIOUtils;

/**
 * Created by amosov-f on 05.09.15.
 */
public enum OkeySearch {
    ;

    public static void main(@NotNull final String... args) {
        final Set<String> urls = new TreeSet<>();
        int count = 0;
        for (int i = 4; i < 148; i++) {
            System.out.println(i);

            final byte[] data;
            try {
                data = MoreIOUtils.read("http://www.okmarket.ru/stores/store/" + i + "/");
            } catch (IOException e) {
                System.err.println(e);
                System.out.println();
                continue;
            }

            final String html = new String(data, StandardCharsets.UTF_8);
            System.out.println(position(html));
            urls(html).forEach(System.out::println);
            urls.addAll(urls(html));
            System.out.println();
            count++;
        }
        System.out.println();
        System.out.println(count);
        urls.forEach(System.out::println);
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
