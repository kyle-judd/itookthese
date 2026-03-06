package io.itookthese.api.enums;

import lombok.Getter;

@Getter
public enum ImageType {
  JPG("image/jpg"),
  PNG("image/png"),
  JPEG("image/jpeg"),
  HEIF("image/heif"),
  HEIC("image/heic");

  private final String mimeType;

  ImageType(String mimeType) {
    this.mimeType = mimeType;
  }
}
