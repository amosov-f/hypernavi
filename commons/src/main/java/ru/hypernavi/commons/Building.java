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

    public Building(@NotNull final GeoPoint location,
                    @NotNull final String address,
                    @NotNull final Double angle) {
        this.location = location;
        this.address = address;
        this.angle = angle;
        this.hasAngle = true;
    }

    public Building(@NotNull final GeoPoint location,
                    @NotNull final String address) {
        this.location = location;
        this.address = address;
        this.angle = 0.0;
        this.hasAngle = false;
    }

    @NotNull
    public GeoPoint getLocation() {
        return location;
    }

    @NotNull
    public String getAddress() {
        return address;
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
            if (hasAngle) {
                obj.put("angle", getAngle());
            }
        } catch (JSONException e) {
            LOG.warn(e.getMessage());
            return null;
        }
        return obj;
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
            LOG.warn("O_o");
            return null;
        }

        Double angle;
        try {
            angle = obj.getDouble("angle");
        } catch (JSONException e) {
            LOG.warn(e.getMessage());
            angle = null;
        }
        if (angle != null) {
            return new Building(location, address, angle);
        } else {
            return new Building(location, address);
        }
    }
}
