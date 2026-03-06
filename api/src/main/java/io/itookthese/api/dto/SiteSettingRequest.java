package io.itookthese.api.dto;

public record SiteSettingRequest(
    String siteTitle, String siteDescription, String contactEmail, String socialLink) {}
