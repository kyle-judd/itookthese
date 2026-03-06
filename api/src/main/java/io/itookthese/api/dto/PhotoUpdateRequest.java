package io.itookthese.api.dto;

public record PhotoUpdateRequest(
    Long categoryId, String title, String description, Integer sortOrder, Boolean featured) {}
