package io.itookthese.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.itookthese.api.dto.SiteSettingRequest;
import io.itookthese.api.dto.SiteSettingResponse;
import io.itookthese.api.entity.SiteSetting;
import io.itookthese.api.repository.SiteSettingRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class SiteSettingServiceTest {

  @Mock private SiteSettingRepository siteSettingRepository;

  @InjectMocks private SiteSettingService siteSettingService;

  private List<SiteSetting> sampleSettings() {
    List<SiteSetting> settings = new ArrayList<>();
    settings.add(SiteSetting.builder().key("site_title").value("My Photography").build());
    settings.add(SiteSetting.builder().key("site_description").value("A portfolio site").build());
    settings.add(SiteSetting.builder().key("contact_email").value("me@example.com").build());
    settings.add(SiteSetting.builder().key("social_link").value("https://instagram.com/me").build());
    return settings;
  }

  @Test
  void getSiteSettings_returnsMappedResponse() {
    when(siteSettingRepository.findAll()).thenReturn(sampleSettings());

    SiteSettingResponse result = siteSettingService.getSiteSettings();

    assertThat(result.siteTitle()).isEqualTo("My Photography");
    assertThat(result.siteDescription()).isEqualTo("A portfolio site");
    assertThat(result.contactEmail()).isEqualTo("me@example.com");
    assertThat(result.socialLink()).isEqualTo("https://instagram.com/me");
  }

  @Test
  void getSiteSettings_missingSetting_returnsNullForMissing() {
    List<SiteSetting> partial = new ArrayList<>();
    partial.add(SiteSetting.builder().key("site_title").value("My Photography").build());
    when(siteSettingRepository.findAll()).thenReturn(partial);

    SiteSettingResponse result = siteSettingService.getSiteSettings();

    assertThat(result.siteTitle()).isEqualTo("My Photography");
    assertThat(result.siteDescription()).isNull();
    assertThat(result.contactEmail()).isNull();
    assertThat(result.socialLink()).isNull();
  }

  @Test
  void getSiteSettings_emptySettings_returnsAllNull() {
    when(siteSettingRepository.findAll()).thenReturn(List.of());

    SiteSettingResponse result = siteSettingService.getSiteSettings();

    assertThat(result.siteTitle()).isNull();
    assertThat(result.siteDescription()).isNull();
    assertThat(result.contactEmail()).isNull();
    assertThat(result.socialLink()).isNull();
  }

  @Test
  void updateSiteSetting_allSettingsPresent_updatesAndReturns() {
    List<SiteSetting> settings = sampleSettings();
    when(siteSettingRepository.findAll()).thenReturn(settings);
    when(siteSettingRepository.saveAll(settings)).thenReturn(settings);

    SiteSettingRequest request =
        new SiteSettingRequest("New Title", "New Description", "new@example.com", "https://twitter.com/me");

    SiteSettingResponse result = siteSettingService.updateSiteSetting(request);

    assertThat(result.siteTitle()).isEqualTo("New Title");
    assertThat(result.siteDescription()).isEqualTo("New Description");
    assertThat(result.contactEmail()).isEqualTo("new@example.com");
    assertThat(result.socialLink()).isEqualTo("https://twitter.com/me");
    verify(siteSettingRepository).saveAll(settings);
  }

  @Test
  void updateSiteSetting_missingSettingKey_throwsException() {
    List<SiteSetting> incomplete = new ArrayList<>();
    incomplete.add(SiteSetting.builder().key("site_description").value("Desc").build());
    incomplete.add(SiteSetting.builder().key("contact_email").value("email@test.com").build());
    incomplete.add(SiteSetting.builder().key("social_link").value("https://social.com").build());
    when(siteSettingRepository.findAll()).thenReturn(incomplete);

    SiteSettingRequest request =
        new SiteSettingRequest("Title", "Desc", "email@test.com", "https://social.com");

    assertThatThrownBy(() -> siteSettingService.updateSiteSetting(request))
        .isInstanceOf(ResponseStatusException.class);
  }

  @Test
  void updateSiteSetting_setsNullValues() {
    List<SiteSetting> settings = sampleSettings();
    when(siteSettingRepository.findAll()).thenReturn(settings);
    when(siteSettingRepository.saveAll(settings)).thenReturn(settings);

    SiteSettingRequest request = new SiteSettingRequest(null, null, null, null);

    SiteSettingResponse result = siteSettingService.updateSiteSetting(request);

    assertThat(result.siteTitle()).isNull();
    assertThat(result.siteDescription()).isNull();
    assertThat(result.contactEmail()).isNull();
    assertThat(result.socialLink()).isNull();
  }
}
