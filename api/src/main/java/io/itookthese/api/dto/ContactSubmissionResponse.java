package io.itookthese.api.dto;

import java.time.OffsetDateTime;

public record ContactSubmissionResponse(
    Long id,
    String name,
    String subject,
    String email,
    String message,
    Boolean isRead,
    OffsetDateTime createdAt) {}
