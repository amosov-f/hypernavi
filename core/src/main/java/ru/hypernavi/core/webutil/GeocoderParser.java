package ru.hypernavi.core.webutil;

import org.jetbrains.annotations.Nullable;


import com.google.inject.Singleton;
import org.json.JSONException;
import org.json.JSONObject;
import ru.hypernavi.util.GeoPointImpl;

/**
 * Created by Константин on 17.08.2015.
 */

@Singleton
@Deprecated
public class GeocoderParser {
    public GeocoderParser() {
    }

    public GeocoderParser(@Nullable final JSONObject response) {
        this.response = response;
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
                        .getJSONObject("AddressDetails")
                        .getJSONObject("Country")
                        .getString("AddressLine");
            }
            address = location;
        } catch (JSONException ignored) {
            address = null;
        }
        return address;
    }

    @Nullable
    public String getCity() {
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
                        .getJSONObject("AddressDetails")
                        .getJSONObject("Country")
                        .getJSONObject("AdministrativeArea")
                        .getJSONObject("SubAdministrativeArea")
                        .getString("SubAdministrativeAreaName");
            }
            address = location;
        } catch (JSONException ignored) {
            address = null;
        }
        return address;
    }

    @Nullable
    public String getLine() {
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
                        .getJSONObject("AddressDetails")
                        .getJSONObject("Country")
                        .getJSONObject("AdministrativeArea")
                        .getJSONObject("SubAdministrativeArea")
                        .getJSONObject("Locality")
                        .getJSONObject("Thoroughfare")
                        .getString("ThoroughfareName");
            }
            address = location;
        } catch (JSONException ignored) {
            address = null;
        }
        return address;
    }

    @Nullable
    public String getNumber() {
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
                        .getJSONObject("AddressDetails")
                        .getJSONObject("Country")
                        .getJSONObject("AdministrativeArea")
                        .getJSONObject("SubAdministrativeArea")
                        .getJSONObject("Locality")
                        .getJSONObject("Thoroughfare")
                        .getJSONObject("Premise")
                        .getString("PremiseNumber");
            }
            address = location;
        } catch (JSONException ignored) {
            address = null;
        }
        return address;
    }


    @Nullable
    public GeoPointImpl getLocation() {
        final GeoPointImpl position;
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
            position = GeoPointImpl.parseString(location);
        } catch (JSONException ignored) {
            return null;
        }
        return position;
    }
}

