package io.itookthese.api.dto;

public record PhotoDetailResponse(
    Long id,
    String title,
    String description,
    String thumbUrl,
    String mediumUrl,
    String fullUrl,
    String placeholderBase64,
    Integer width,
    Integer height,
    Boolean isFeatured,
    String category,
    Long categoryId,
    Integer sortOrder,
    String cameraModel,
    String lens,
    String focalLength,
    String aperture,
    String shutterSpeed,
    String iso,
    String createdAt) {}
