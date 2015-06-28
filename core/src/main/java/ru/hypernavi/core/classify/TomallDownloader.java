package ru.hypernavi.core.classify;

import com.google.common.primitives.Ints;
import com.google.common.util.concurrent.RateLimiter;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import ru.hypernavi.core.Category;
import ru.hypernavi.core.Good;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: amosov-f
 * Date: 23.11.14
 * Time: 1:28
 */
final class TomallDownloader {
    private static final Logger LOG = Logger.getLogger(TomallDownloader.class.getName());

    private static final String URL = "http://tomall.ru/allmarket/";
    private static final int MIN_ID = 10;
    private static final int MAX_ID = 164076;
    private static final int RPS = 3;

    private static final int N_THREADS = 10;
    private static final int MAX_TASKS_IN_MEMORY = 100;
    private static final int MAX_GOODS_IN_MEMORY = 10000;

    private static final int CONNECT_TIMEOUT = 1000;
    private static final int SOCKET_TIMEOUT = 30000;

    private final int minId;
    private final int maxId;

    @NotNull
    private final RateLimiter limiter = RateLimiter.create(RPS);
    @NotNull
    private final BlockingQueue<Good> goods = new LinkedBlockingQueue<>(MAX_GOODS_IN_MEMORY);
    @NotNull
    private final ExecutorService service = new ThreadPoolExecutor(
            N_THREADS,
            N_THREADS,
            Long.MAX_VALUE,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(MAX_TASKS_IN_MEMORY),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );
    @NotNull
    private final HttpClient httpClient = httpClient();

    public TomallDownloader(final int minId, final int maxId) {
        this.minId = minId;
        this.maxId = maxId;
    }

    @NotNull
    public Iterable<Good> getGoods() {
        return new TomallGoods();
    }

    @Nullable
    private static Good parse(final int id, @NotNull final String html) throws IOException {
        try {
            final Element element = child(Jsoup.parse(html).body(), 0, 1, 0, 0);
            final String name = child(element, 1, 0, 0).text();
            final Category category = Category.parse(child(element, 2, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0).text());
            return category != null ? new Good(id, name, category) : null;
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    @NotNull
    private static Element child(@NotNull final Element el, @NotNull final int... indices) {
        return Ints.asList(indices).stream().reduce(el, Element::child, (el1, el2) -> el2);
    }

    @NotNull
    private static HttpClient httpClient() {
        final PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(N_THREADS);
        connectionManager.setDefaultMaxPerRoute(N_THREADS);
        return HttpClientBuilder.create()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(CONNECT_TIMEOUT)
                        .setSocketTimeout(SOCKET_TIMEOUT)
                        .build())
                .build();
    }

    private final class TomallGoods implements Iterable<Good> {
        public TomallGoods() {
            new Thread(() -> {
                for (int id = minId; id <= maxId; id++) {
                    final int curId = id;
                    service.submit(() -> {
                        final HttpGet request = new HttpGet(URL + curId);
                        limiter.acquire();
                        try {
                            final HttpResponse response = httpClient.execute(request);
                            EntityUtils.consumeQuietly(response.getEntity());
                            final Good good = parse(curId, IOUtils.toString(
                                    response.getEntity().getContent(),
                                    "windows-1251"
                            ));
                            if (good != null) {
                                goods.put(good);
                            }
                        } catch (IOException | InterruptedException e) {
                            LOG.log(Level.WARNING, "DEBUG URL: " + request, e);
                        }
                    });
                }
                service.shutdown();
            }).start();
        }

        @Override
        public Iterator<Good> iterator() {
            return new Iterator<Good>() {
                @Nullable
                private Good nextGood = null;

                @Override
                public boolean hasNext() {
                    if (nextGood != null) {
                        return true;
                    }
                    try {
                        nextGood = goods.poll(SOCKET_TIMEOUT, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    return nextGood != null;
                }

                @NotNull
                @Override
                public Good next() {
                    if (nextGood == null) {
                        throw new NoSuchElementException("Check more elements before!");
                    }
                    final Good tempNextGood = nextGood;
                    nextGood = null;
                    return tempNextGood;
                }
            };
        }
    }

    public static void main(@NotNull final String[] args) throws IOException {
        int minId = MIN_ID;
        final InputStream in = TomallDownloader.class.getResourceAsStream("/tomall/goods.txt");
        if (in != null) {
            for (final String line : IOUtils.readLines(in)) {
                final String[] parts = line.split("\t");
                minId = Math.max(minId, Integer.parseInt(parts[0]) + 1);
            }
        }

        final FileWriter fout = new FileWriter("src/main/resources/tomall/goods.txt", true);
        final TomallDownloader downloader = new TomallDownloader(minId, MAX_ID);
        for (final Good good : downloader.getGoods()) {
            fout.write(good + "\n");
            fout.flush();
            LOG.info(good.toString());
        }
        fout.close();
    }
}
