package ru.hypernavi.core.telegram;

import org.jetbrains.annotations.NotNull;
import ru.hypernavi.commons.Image;
import ru.hypernavi.commons.hint.Plan;
import ru.hypernavi.util.awt.ImageUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Optional;

/**
 * Created by amosov-f on 29.11.16
 */
public final class LocationImage {
  @NotNull
  private final Plan plan;
  @NotNull
  private final BufferedImage map;
  @NotNull
  private final Point location;

  public LocationImage(@NotNull final Plan plan, @NotNull final BufferedImage map, @NotNull final Point location) {
    this.plan = plan;
    this.map = map;
    this.location = location;
  }

  @NotNull
  public BufferedImage getMap() {
    return map;
  }

  @NotNull
  public Image.Format getFormat() {
    return Optional.ofNullable(ImageUtils.format(map))
        .map(Image.Format::parse)
        .orElse(plan.getImage().getFormat(Image.Format.JPG));
  }

  public boolean isLocationInsideMap() {
    return checkLocationInsideImage(map, location);
  }

  public static boolean checkLocationInsideImage(@NotNull final BufferedImage image, @NotNull final Point location) {
    return checkBetween(location.x, 0, image.getWidth())
        && checkBetween(location.y, 0, image.getHeight());
  }

  public static boolean checkLocationInsideImage(@NotNull final Image image, @NotNull final Point location) {
    return checkBetween(location.x, 0, image.getDimension().getWidth())
        && checkBetween(location.y, 0, image.getDimension().getHeight());
  }

  private static boolean checkBetween(final int x, final int l, final int r) {
    return l <= x && x < r;
  }
}
