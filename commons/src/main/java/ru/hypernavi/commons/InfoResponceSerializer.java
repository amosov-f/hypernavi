package ru.hypernavi.commons;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.hypernavi.util.GeoPoint;

/**
 * Created by Константин on 15.08.2015.
 */
public enum InfoResponceSerializer {
    ;
    private static final Log LOG = LogFactory.getLog(InfoResponceSerializer.class);


    @Nullable
    public static JSONObject serialize(final InfoResponce responce) {
        final JSONObject jsonResponse;
        try {
            jsonResponse = new JSONObject();
            final JSONArray hypermarkets = new JSONArray();
            if (responce.getClosestMarkets() != null) {
                for (int i = 0; i < responce.getClosestMarkets().size(); ++i) {
                    hypermarkets.put(HypermarketSerializer.serialize(responce.getClosestMarkets().get(i)));
                }
            }
            final JSONObject metainfo = new JSONObject();
            final JSONObject data = new JSONObject();

            metainfo.put("lontitude", responce.getLocation().getLongitude());
            metainfo.put("latitude", responce.getLocation().getLatitude());
            metainfo.put("iscorrect", true);

            data.put("hypermarkets", hypermarkets);

            jsonResponse.put("data", data);
            jsonResponse.put("meta", metainfo);

        } catch (JSONException e) {
            LOG.warn(e.getMessage());
            return null;
        }
        return jsonResponse;
    }

    @Nullable
    public static InfoResponce deserialize(final JSONObject jsonObject) {
        final GeoPoint location;
        final List<Hypermarket> markets = new ArrayList<>();

        try {
            final JSONObject data = jsonObject.getJSONObject("data");
            final JSONObject meta = jsonObject.getJSONObject("meta");

            final JSONArray jsonMarkets = data.getJSONArray("hypermarkets");
            for (int i = 0; i < jsonMarkets.length(); ++i) {
                markets.add(HypermarketSerializer.deserialize(jsonMarkets.getJSONObject(i)));
            }

            final double longitude = meta.getDouble("longitude");
            final double latitude = meta.getDouble("latitude");
            location = new GeoPoint(latitude, longitude);
        } catch (JSONException e) {
            LOG.warn(e.getMessage());
            return null;
        }
        return new InfoResponce(markets, location);
    }
}
