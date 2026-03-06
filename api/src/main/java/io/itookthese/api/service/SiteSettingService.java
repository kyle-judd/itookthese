package io.itookthese.api.service;

import io.itookthese.api.dto.SiteSettingRequest;
import io.itookthese.api.dto.SiteSettingResponse;
import io.itookthese.api.entity.SiteSetting;
import io.itookthese.api.enums.SiteSettingKey;
import io.itookthese.api.repository.SiteSettingRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class SiteSettingService {
  private final SiteSettingRepository siteSettingRepository;

  public SiteSettingResponse getSiteSettings() {
    List<SiteSetting> settings = siteSettingRepository.findAll();
    String siteTitle = getKey(settings, SiteSettingKey.TITLE);
    String siteDescription = getKey(settings, SiteSettingKey.DESCRIPTION);
    String contactEmail = getKey(settings, SiteSettingKey.EMAIL);
    String socialLink = getKey(settings, SiteSettingKey.SOCIAL);
    return new SiteSettingResponse(siteTitle, siteDescription, contactEmail, socialLink);
  }

  @Transactional
  public SiteSettingResponse updateSiteSetting(SiteSettingRequest siteSettingRequest) {
    updateSetting(SiteSettingKey.TITLE, siteSettingRequest.siteTitle());
    updateSetting(SiteSettingKey.DESCRIPTION, siteSettingRequest.siteDescription());
    updateSetting(SiteSettingKey.EMAIL, siteSettingRequest.contactEmail());
    updateSetting(SiteSettingKey.SOCIAL, siteSettingRequest.socialLink());
    return getSiteSettings();
  }

  private String getKey(List<SiteSetting> settings, SiteSettingKey key) {
    return settings.stream()
        .filter(setting -> key.getKey().equals(setting.getKey()))
        .map(SiteSetting::getValue)
        .findFirst()
        .orElse(null);
  }

  private void updateSetting(SiteSettingKey key, String value) {
    SiteSetting setting =
        siteSettingRepository
            .findByKey(key.getKey())
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR, "Missing setting: " + key.getKey()));
    setting.setValue(value);
    siteSettingRepository.save(setting);
  }
}
