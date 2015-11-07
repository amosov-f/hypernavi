package ru.hypernavi.commons;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import ru.hypernavi.util.GeoPoint;

/**
 * Created by Константин on 14.09.2015.
 */
@Deprecated
public class Building {
    private static final Log LOG = LogFactory.getLog(Building.class);

    @NotNull
    private final GeoPoint location;
    @NotNull
    private final String address;
    @NotNull
    private final Double angle;
    @NotNull
    private final Boolean hasAngle;

    private final String number;
    private final String line;
    private final String city;

    public Building(@NotNull final GeoPoint location,
                    @NotNull final String address,
                    @NotNull final Double angle,
                    @Nullable final String city,
                    @Nullable final String line,
                    @Nullable final String number) {
        this.location = location;
        this.address = address;
        this.angle = angle;
        this.hasAngle = true;
        this.number = number;
        this.city = city;
        this.line = line;
    }

    public Building(@NotNull final GeoPoint location,
                    @NotNull final String address,
                    @Nullable final String city,
                    @Nullable final String line,
                    @Nullable final String number) {
        this.location = location;
        this.address = address;
        this.angle = 0.0;
        this.hasAngle = false;
        this.number = number;
        this.city = city;
        this.line = line;
    }

    @NotNull
    public GeoPoint getLocation() {
        return location;
    }

    @NotNull
    public String getAddress() {
        return address;
    }


    @Nullable
    public String getCity() {
        return city;
    }


    @Nullable
    public String getLine() {
        return line;
    }


    @Nullable
    public String getNumber() {
        return number;
    }


    @NotNull
    public Double getAngle() {
        return angle;
    }

    @NotNull
    public Boolean hasAngle() {
        return hasAngle;
    }

    @Nullable
    public JSONObject toJSON() {
        final JSONObject obj = new JSONObject();
        try {
            obj.put("location", location.toJSON());
            obj.put("address", getAddress());
            obj.put("line", getLine());
            obj.put("city", getCity());
            obj.put("number", getNumber());
            if (hasAngle) {
                obj.put("angle", getAngle());
            }
        } catch (JSONException e) {
            LOG.warn(e.getMessage());
            return null;
        }
        return obj;
    }


    private static String getParametr(final String name, final JSONObject obj) {
        try {
            return obj.getString(name);
        } catch (JSONException ignored) {
            return null;
        }
    }

    @Nullable
    public static Building construct(@NotNull final JSONObject obj) {
        final GeoPoint location;
        final String address;
        try {
            location = GeoPoint.construct(obj.getJSONObject("location"));
            address = obj.getString("address");
        } catch (JSONException e) {
            LOG.warn(e.getMessage());
            return null;
        }

        if (location == null || address == null) {
            return null;
        }

        final Double angle;
        if (getParametr("angle", obj) != null) {
            angle = Double.parseDouble(getParametr("angle", obj));
        } else{
            angle = null;
        }
        final String city = getParametr("city", obj);
        final String number = getParametr("number", obj);
        final String line = getParametr("line", obj);


        if (angle != null) {
            return new Building(location, address, angle, city, line, number);
        } else {
            return new Building(location, address, city, line, number);
        }



    }
}
