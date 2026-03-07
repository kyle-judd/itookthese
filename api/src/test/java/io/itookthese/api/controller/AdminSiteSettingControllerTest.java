package io.itookthese.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.itookthese.api.config.GlobalExceptionHandler;
import io.itookthese.api.dto.SiteSettingRequest;
import io.itookthese.api.dto.SiteSettingResponse;
import io.itookthese.api.service.SiteSettingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class AdminSiteSettingControllerTest {

  @Mock private SiteSettingService siteSettingService;
  @InjectMocks private AdminSiteSettingController adminSiteSettingController;
  private MockMvc mockMvc;
  private final ObjectMapper objectMapper = new ObjectMapper();

  private final SiteSettingResponse sampleSettings =
      new SiteSettingResponse(
          "iTookThese", "A photography portfolio", "admin@itookthese.com", "https://instagram.com");

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(adminSiteSettingController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
  }

  @Test
  void getSettings_returnsSettings() throws Exception {
    when(siteSettingService.getSiteSettings()).thenReturn(sampleSettings);

    mockMvc
        .perform(get("/api/v1/admin/settings"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.siteTitle").value("iTookThese"))
        .andExpect(jsonPath("$.contactEmail").value("admin@itookthese.com"));
  }

  @Test
  void updateSettings_returnsUpdated() throws Exception {
    SiteSettingRequest request =
        new SiteSettingRequest("New Title", "New desc", "new@email.com", "https://twitter.com");
    SiteSettingResponse updated =
        new SiteSettingResponse("New Title", "New desc", "new@email.com", "https://twitter.com");
    when(siteSettingService.updateSiteSetting(any())).thenReturn(updated);

    mockMvc
        .perform(
            put("/api/v1/admin/settings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.siteTitle").value("New Title"));
  }
}
