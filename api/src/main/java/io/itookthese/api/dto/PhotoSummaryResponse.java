package io.itookthese.api.dto;

public record PhotoSummaryResponse(
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
    Integer sortOrder) {}
