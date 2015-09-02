package ru.hypernavi.core.webutil;

import com.google.inject.Singleton;
import org.jetbrains.annotations.Nullable;


import org.json.JSONException;
import org.json.JSONObject;
import ru.hypernavi.util.GeoPoint;

/**
 * Created by Константин on 17.08.2015.
 */

@Singleton
public class GeocoderParser {
    public GeocoderParser() {
    }

    @Nullable
    private JSONObject response = null;

    public void setResponce(final JSONObject response) {
        this.response = response;
    }

    @Nullable
    public String getAddress() {
        String address;
        try {
            String location = "";
            if (response != null) {
                location = response.getJSONObject("response")
                        .getJSONObject("GeoObjectCollection")
                        .getJSONArray("featureMember")
                        .getJSONObject(0)
                        .getJSONObject("GeoObject")
                        .getJSONObject("metaDataProperty")
                        .getJSONObject("GeocoderMetaData")
                        .getString("text");
            }
            address = location;
        } catch (JSONException ignored) {
            address = null;
        }
        return address;
    }

    @Nullable
    public GeoPoint getLocation() {
        final GeoPoint position;
        try {
            String location = "";
            if (response != null) {
                location = response.getJSONObject("response")
                        .getJSONObject("GeoObjectCollection")
                        .getJSONArray("featureMember")
                        .getJSONObject(0)
                        .getJSONObject("GeoObject")
                        .getJSONObject("Point")
                        .getString("pos");
            }
            position = GeoPoint.parseString(location);
        } catch (JSONException ignored) {
            return null;
        }
        return position;
    }
}

