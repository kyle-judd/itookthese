package io.itookthese.api.controller;

import io.itookthese.api.dto.SiteSettingRequest;
import io.itookthese.api.dto.SiteSettingResponse;
import io.itookthese.api.service.SiteSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/settings")
@RequiredArgsConstructor
public class AdminSiteSettingController {

  private final SiteSettingService siteSettingService;

  @PutMapping
  public ResponseEntity<SiteSettingResponse> updateSettings(@RequestBody SiteSettingRequest siteSettingRequest) {
    return ResponseEntity.status(HttpStatus.OK).body(siteSettingService.updateSiteSetting(siteSettingRequest));
  }

  @GetMapping
  public ResponseEntity<SiteSettingResponse> getAllSettings() {
      return ResponseEntity.status(HttpStatus.OK).body(siteSettingService.getSiteSettings());
  }
}
