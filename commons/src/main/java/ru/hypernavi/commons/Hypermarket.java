package ru.hypernavi.commons;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import ru.hypernavi.util.GeoPoint;

/**
 * Created by Константин on 28.07.2015.
 */
public class Hypermarket implements Positioned, Indexable {
    private static final Log LOG = LogFactory.getLog(Hypermarket.class);

    private final int id;
    @NotNull
    private final String type;

    @NotNull
    private final Building build;

    @NotNull
    private final WebInfo web;

    /*public Hypermarket(final int id,
                       @NotNull final GeoPoint location,
                       @NotNull final String address,
                       @NotNull final String type,
                       @NotNull final String path,
                       @NotNull final String url,
                       @NotNull final String page,
                       final double angle) {
        this.build = new Building(location, address);
        this.web = new WebInfo(path, url, page);

        this.id = id;
        this.type = type;

        this.location = location;
        this.address = address;
        this.path = path;
        this.url = url;
        this.angle = angle;
        this.page = page;
    }
*/
    public Hypermarket(final int id,
                       @NotNull final Building build,
                       @NotNull final String type,
                       @NotNull final WebInfo web) {
        this.build = build;
        this.web = web;

        this.id = id;
        this.type = type;
    }


    @NotNull
    public String getUrl() {
        return web.getUrl();
    }

    public double getAngle() {
        return build.getAngle();
    }

    @NotNull
    @Override
    public GeoPoint getLocation() {
        return build.getLocation();
    }

    @Override
    public int getId() {
        return id;
    }

    @NotNull
    public String getPath() {
        return web.getPath();
    }

    @NotNull
    public String getType() {
        return type;
    }

    @NotNull
    public String getAddress() {
        return build.getAddress();
    }

    @NotNull
    public String getPage() {
        return web.getPage();
    }

    @NotNull
    public Building getBuild() {
        return build;
    }

    @NotNull
    public WebInfo getWeb() {
        return web;
    }

    @Nullable
    public JSONObject toJSON() {
        final JSONObject obj = new JSONObject();
        try {
            obj.put("web", getWeb().toJSON());
            obj.put("build",  getBuild().toJSON());
            obj.put("id", getId());
            obj.put("type", getType());
        } catch (JSONException e) {
            LOG.info(e.getMessage());
            return null;
        }
        return obj;
    }

    @Nullable
    public static Hypermarket construct(@NotNull final JSONObject obj) {
        final WebInfo web;
        final Building build;
        final int id;
        final String type;

        try {
            web = WebInfo.construct(obj.getJSONObject("web"));
            build = Building.construct(obj.getJSONObject("build"));
            id = obj.getInt("id");
            type = obj.getString("type");
        } catch (JSONException ignored) {
            return null;
        }
        if (web == null || build == null) {
            LOG.info(web + " " + build);
            return null;
        }

        return new Hypermarket(id, build, type, web);
    }
}
