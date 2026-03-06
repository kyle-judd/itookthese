package io.itookthese.api.dto;

public record ImageProcessingResult(
    String filenameThumb,
    String filenameMedium,
    String filenameFull,
    String placeholderBase64,
    String exifData,
    Integer width,
    Integer height) {}
