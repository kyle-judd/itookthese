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

  @Transactional(readOnly = true)
  public SiteSettingResponse getSiteSettings() {
    List<SiteSetting> settings = siteSettingRepository.findAll();
    return mapToResponse(settings);
  }

  @Transactional
  public SiteSettingResponse updateSiteSetting(SiteSettingRequest siteSettingRequest) {
    List<SiteSetting> settings = siteSettingRepository.findAll();
    updateSettingInList(settings, SiteSettingKey.TITLE, siteSettingRequest.siteTitle());
    updateSettingInList(settings, SiteSettingKey.DESCRIPTION, siteSettingRequest.siteDescription());
    updateSettingInList(settings, SiteSettingKey.EMAIL, siteSettingRequest.contactEmail());
    updateSettingInList(settings, SiteSettingKey.SOCIAL, siteSettingRequest.socialLink());
    siteSettingRepository.saveAll(settings);
    return mapToResponse(settings);
  }

  private void updateSettingInList(List<SiteSetting> settings, SiteSettingKey key, String value) {
    settings.stream()
        .filter(s -> key.getKey().equals(s.getKey()))
        .findFirst()
        .orElseThrow(
            () ->
                new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Missing setting: " + key.getKey()))
        .setValue(value);
  }

  private SiteSettingResponse mapToResponse(List<SiteSetting> settings) {
    return new SiteSettingResponse(
        getValue(settings, SiteSettingKey.TITLE),
        getValue(settings, SiteSettingKey.DESCRIPTION),
        getValue(settings, SiteSettingKey.EMAIL),
        getValue(settings, SiteSettingKey.SOCIAL));
  }

  private String getValue(List<SiteSetting> settings, SiteSettingKey key) {
    return settings.stream()
        .filter(s -> key.getKey().equals(s.getKey()))
        .map(SiteSetting::getValue)
        .findFirst()
        .orElse(null);
  }
}
