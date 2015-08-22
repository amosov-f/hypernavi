package ru.hypernavi.commons;

import org.json.JSONException;
import org.json.JSONObject;
import ru.hypernavi.util.GeoPoint;

/**
 * Created by Константин on 15.08.2015.
 */
public enum HypermarketSerializer {
    ;

    public static JSONObject serialize(final Hypermarket market) {
        final JSONObject obj = new JSONObject();
        try {
            obj.put("url", market.getUrl());
            obj.put("latitude", market.getLocation().getLatitude());
            obj.put("longitude", market.getLocation().getLongitude());
            obj.put("type", market.getType());
            obj.put("address", market.getAddress());
            obj.put("id", market.getId());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return obj;
    }

    public static Hypermarket deserialize(final JSONObject jsonObject) {
        final int id;
        final GeoPoint location;
        final String address;
        final String type;
        final String url;
        try {
            id = jsonObject.getInt("id");
            final double longitude = jsonObject.getDouble("longitude");
            final double latitude = jsonObject.getDouble("latitude");
            location = new GeoPoint(longitude, latitude);
            address = jsonObject.getString("address");
            type = jsonObject.getString("type");
            url = jsonObject.getString("url");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return new Hypermarket(id, location, address, type, url);
    }
}
