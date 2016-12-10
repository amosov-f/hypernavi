package ru.hypernavi.core.telegram.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by amosov-f on 11.12.16
 */
public final class PhotoSize {
  public static final PhotoSize[] EMPTY_ARRAY = new PhotoSize[0];

  @NotNull
  private final String fileId;
  private final int width;
  private final int height;
  @Nullable
  private final Integer fileSize;

  public PhotoSize(@NotNull final String fileId, final int width, final int height, @Nullable final Integer fileSize) {
    this.fileId = fileId;
    this.width = width;
    this.height = height;
    this.fileSize = fileSize;
  }

  @NotNull
  public String getFileId() {
    return fileId;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  @Nullable
  public Integer getFileSize() {
    return fileSize;
  }
}
