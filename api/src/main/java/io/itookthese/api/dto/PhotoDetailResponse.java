package io.itookthese.api.dto;

public record PhotoDetailResponse(
    Long id,
    String title,
    String thumbUrl,
    String placeholderBase64,
    Integer width,
    Integer height,
    Boolean isFeatured,
    CategoryResponse category,
    String description,
    String mediumUrl,
    String fullUrl,
    String exifData) {}
