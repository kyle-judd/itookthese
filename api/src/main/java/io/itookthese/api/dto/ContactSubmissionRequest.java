package io.itookthese.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ContactSubmissionRequest(
    @NotBlank @Size(min = 2, max = 100) String name,
    @Email @NotBlank String email,
    @NotBlank @Size(min = 5, max = 200) String subject,
    @NotBlank @Size(min = 10, max = 5000) String message,
    String honeypot) {}
