package ru.hypernavi.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 * Created by Константин on 02.09.2015.
 */
public class URLdownload {
    private static final Log LOG = LogFactory.getLog(URLdownload.class);

    @NotNull
    private final HttpClient client = HttpClientBuilder.create()
            .disableContentCompression()
            .setMaxConnPerRoute(100)
            .setMaxConnTotal(10)
            .build();


    @Nullable
    public byte[] load(@NotNull final String pathurl) {
        byte[] data = null;
        try {
            final HttpResponse response = client.execute(new HttpGet(pathurl));
            data = IOUtils.toByteArray(response.getEntity().getContent());
        } catch (IOException e) {
            LOG.warn("Can not download data\n" + e.getMessage());
        }
        return data;
    }
}
