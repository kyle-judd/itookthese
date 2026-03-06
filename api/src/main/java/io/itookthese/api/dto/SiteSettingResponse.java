package io.itookthese.api.dto;

public record SiteSettingResponse(
    String siteTitle, String siteDescription, String contactEmail, String socialLink) {}
