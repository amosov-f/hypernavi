package ru.hypernavi.commons;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.hypernavi.util.GeoPointImpl;

/**
 * Created by Константин on 15.08.2015.
 */
@Deprecated
public enum InfoResponceSerializer {
    ;
    private static final Log LOG = LogFactory.getLog(InfoResponceSerializer.class);


    @Nullable
    public static JSONObject serialize(final InfoResponse responce) {
        final JSONObject jsonResponse;
        try {
            jsonResponse = new JSONObject();
            final JSONArray hypermarkets = new JSONArray();
            if (responce.getClosestMarkets() != null) {
                for (int i = 0; i < responce.getClosestMarkets().size(); ++i) {
                    hypermarkets.put(responce.getClosestMarkets().get(i).toJSON());
                }
            }
            final JSONObject metainfo = new JSONObject();
            final JSONObject data = new JSONObject();

            metainfo.put("longitude", responce.getLocation().getLongitude());
            metainfo.put("latitude", responce.getLocation().getLatitude());

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
    public static InfoResponse deserialize(final JSONObject jsonObject) {
        final GeoPointImpl location;
        final List<Hypermarket> markets = new ArrayList<>();

        try {
            final JSONObject data = jsonObject.getJSONObject("data");
            final JSONObject meta = jsonObject.getJSONObject("meta");

            final JSONArray jsonMarkets = data.getJSONArray("hypermarkets");
            for (int i = 0; i < jsonMarkets.length(); ++i) {
                markets.add(Hypermarket.construct(jsonMarkets.getJSONObject(i)));
            }

            final double longitude = meta.getDouble("longitude");
            final double latitude = meta.getDouble("latitude");
            location = new GeoPointImpl(longitude, latitude);
        } catch (JSONException e) {
            LOG.warn(e.getMessage());
            return null;
        }
        return new InfoResponse(markets, location);
    }
}
