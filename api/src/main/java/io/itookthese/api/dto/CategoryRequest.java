package io.itookthese.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
    @NotBlank @Size(max = 100) String name,
    String description,
    @NotBlank @Size(max = 100) String slug,
    Integer sortOrder) {}
