package ru.hyper.core.load;

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
import ru.hyper.core.model.Category;
import ru.hyper.core.model.Good;

import java.io.*;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.*;

/**
 * User: amosov-f
 * Date: 23.11.14
 * Time: 1:28
 */
public class TomallDownloader {
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
    private final ExecutorService service = new ThreadPoolExecutor(N_THREADS, N_THREADS, Long.MAX_VALUE, TimeUnit.SECONDS, new LinkedBlockingQueue<>(MAX_TASKS_IN_MEMORY), new ThreadPoolExecutor.CallerRunsPolicy());
    @NotNull
    private final HttpClient httpClient = httpClient();

    public TomallDownloader(final int minId, final int maxId) {
        this.minId = minId;
        this.maxId = maxId;
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
                        .setSocketTimeout(SOCKET_TIMEOUT).build()).build();
    }

    public static void main(@NotNull String[] args) throws IOException {
        int minId = MIN_ID;
        final InputStream in = TomallDownloader.class.getResourceAsStream("/tomall/goods.txt");
        if (in != null) {
            final BufferedReader fin = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = fin.readLine()) != null) {
                final String[] parts = line.split("\t");
                minId = Math.max(minId, Integer.parseInt(parts[0]) + 1);
            }
        }

        final FileWriter fout = new FileWriter("src/main/resources/tomall/goods.txt", true);
        final TomallDownloader downloader = new TomallDownloader(minId, MAX_ID);
        for (final Good good : downloader.getGoods()) {
            fout.write(good + "\n");
            fout.flush();
            System.out.println(good);
        }
        fout.close();
    }

    @NotNull
    public Iterable<Good> getGoods() {
        return new TomallGoods();
    }

    private static final class GoodParser {
        @Nullable
        public static Good parse(final int id, @NotNull final String html) throws IOException {
            try {
                final Element element = Jsoup.parse(html).body().child(0).child(1).child(0).child(0);
                final String name = element.child(1).child(0).child(0).text();
                final Category category = Category.parse(element.child(2).child(1).child(0).child(0).child(0).child(1).child(0).child(0).child(0).child(1).child(0).text());
                if (category == null) {
                    return null;
                }
                return new Good(id, name, category);
            } catch (IndexOutOfBoundsException e) {
                return null;
            }
        }
    }

    private class TomallGoods implements Iterable<Good> {
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
                            final Good good = GoodParser.parse(curId, IOUtils.toString(response.getEntity().getContent(), "windows-1251"));
                            if (good != null) {
                                goods.put(good);
                            }
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                            System.err.println("DEBUG URL: " + request);
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
}
