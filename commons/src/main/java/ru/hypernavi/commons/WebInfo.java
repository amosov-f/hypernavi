package ru.hypernavi.commons;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Константин on 15.09.2015.
 */
public class WebInfo {
    @NotNull
    private final String path;
    @NotNull
    private final String url;
    @NotNull
    private final String page;

    public WebInfo(@NotNull final String path,
                   @NotNull final String url,
                   @NotNull final String page) {
        this.page = page;
        this.path = path;
        this.url = url;
    }


    @NotNull
    public String getPath() {
        return path;
    }

    @NotNull
    public String getUrl() {
        return url;
    }

    @NotNull
    public String getPage() {
        return page;
    }

    @Nullable
    public JSONObject toJSON() {
        final JSONObject obj = new JSONObject();
        try {
            obj.put("page", getPage());
            obj.put("url",  getUrl());
            obj.put("path", getPath());
        } catch (JSONException ignored) {
            return null;
        }
        return obj;
    }

    @Nullable
    public static WebInfo construct(@NotNull final JSONObject obj) {
        final String path;
        final String page;
        final String url;
        try {
            path = obj.getString("path");
            page = obj.getString("page");
            url = obj.getString("url");
        } catch (JSONException ignored) {
            return null;
        }

        return new WebInfo(path, url, page);
    }


}
