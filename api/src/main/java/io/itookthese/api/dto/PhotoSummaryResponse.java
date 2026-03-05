package io.itookthese.api.dto;

public record PhotoSummaryResponse(
    Long id,
    String title,
    String thumbUrl,
    String placeholderBase64,
    Integer width,
    Integer height,
    Boolean isFeatured,
    CategoryResponse category) {}
