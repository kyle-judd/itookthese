package io.itookthese.api.enums;

import lombok.Getter;

@Getter
public enum SiteSettingKey {
  TITLE("site_title"),
  DESCRIPTION("site_description"),
  EMAIL("contact_email"),
  SOCIAL("social_link");

  private final String key;

  SiteSettingKey(String key) {
    this.key = key;
  }
}
