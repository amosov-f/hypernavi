package ru.hypernavi.commons.hint;

import net.jcip.annotations.Immutable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.hypernavi.commons.Image;
import ru.hypernavi.commons.PointMap;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by amosov-f on 07.11.15.
 */
@Immutable
public final class Plan extends Picture {
    public static final Plan[] EMPTY_ARRAY = new Plan[0];
    public static final Type TYPE = Type.PLAN;

    public static final String X_MODEL_KEY = "x";
    public static final String Y_MODEL_KEY = "y";

    @Nullable
    private final Double azimuth;
    @NotNull
    private final PointMap[] points;
    @Nullable
    private Map<String, String> models;

    public Plan(@Nullable final String description, @NotNull final Image image, @Nullable final Double azimuth, @NotNull final PointMap... points) {
        super(description, image);
        this.azimuth = azimuth;
        this.points = points;
    }

    @Nullable
    public Double getAzimuth() {
        return azimuth;
    }

    @NotNull
    public PointMap[] getPoints() {
        // TODO
        return points != null ? points : PointMap.EMPTY_ARRAY;
    }

    public void setModel(@NotNull final String key, @NotNull final String serializedModel) {
        if (models == null) {
            models = new HashMap<>();
        }
        this.models.put(key, serializedModel);
    }

    @Nullable
    public String getModel(@NotNull final String key) {
        return models != null ? models.get(key) : null;
    }

    @NotNull
    @Override
    public Type getType() {
        return TYPE;
    }
}
